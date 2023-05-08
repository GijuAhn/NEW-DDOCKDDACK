<template>
  <div id="view" v-if="games">
    <div id="searchBar" v-show="isShow">
      <div>
        <input
          @keyup.enter="callApi"
          type="text"
          v-model.trim="pageConditionReq.keyword"
          placeholder="검색어를 입력해주세요"
        />
        <button id="btn-s" @click="callApi">검색</button>
      </div>
    </div>
    <div id="list" v-if="isShow">
      <single-game
        v-for="(game, index) in games"
        :key="index"
        :game="game"
        :index="index"
        @ready="(value) => ready(value)"
      ></single-game>
    </div>
    <div id="ifTotalPagesIsZero" v-if="totalPages === 0">
      <p>게임 검색 결과가 없습니다.</p>
    </div>
    <div v-if="!isShow">
      <div>
        <button class="backButton" @click="back">목록으로</button>
      </div>
      <div class="container" v-show="!isShow">
        <div>
          <img
            id="gameImage"
            :src="`${IMAGE_PATH}/${games[targetGameIdx].thumbnail}`"
          />
        </div>
        <div>
          <img
            v-if="uploadImage"
            :src="convertFile(uploadImage)"
            id="imgElement"
            alt="이미지 미리보기"
          />
        </div>
        <input
          type="file"
          @change="fileUploadEvent"
          accept=".jpg,.jpeg,.png, .jfif"
          id="fileInput"
          style="display: none"
        />
        <label for="fileInput" class="file-label">
          <button class="selectButton">사진 올리기</button>
        </label>
        <div id="etcSection" v-if="!isShow">
          <div class="myProgress">
            <div class="myBar"></div>
            <div class="percent">{{ per }}%</div>
          </div>
          <div>
            <button @click="analysis" class="captureButton">분석</button>
          </div>
        </div>
      </div>

      <div id="board" v-if="rank">
        <leader-board
          :rank="rank"
          @getImage="(image) => openRankingImageModal(image)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useStore } from "vuex";
import SingleGame from "@/components/GameList/item/SingleGame.vue";
import LeaderBoard from "@/components/GameList/item/LeaderBoard.vue";
import html2canvas from "html2canvas";
import heic2any from "heic2any";

const api = apiInstance();
const store = useStore();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const games = ref();
const rank = ref();
const isShow = ref(true);
const captureMode = ref(false);
const targetGameIdx = ref(0);
const uploadImage = ref();
const per = ref(0);
const mode = ref("");
const hasNext = ref();

const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

let totalPages = ref();

let pageConditionReq = ref({
  keyword: "",
  page: 1,
});

const ready = (value) => {
  targetGameIdx.value = value.index;
  isShow.value = false;

  api
    .get(`/api/ranks/${games.value[value.index].id}`)
    .then((response) => {
      rank.value = response.data;
    })
    .catch((error) => {
      console.log(error);
    });
};

const callApi = () => {
  api
    .get(`/api/single-games`, {
      params: {
        keyword: pageConditionReq.value.keyword,
        page: pageConditionReq.value.page - 1,
      },
      headers: { "access-token": accessToken },
    })
    .then((response) => {
      games.value = response.data.content;
      totalPages = response.data.totalPages;
    })
    .catch((error) => {
      console.log(error);
    });
};

const analysis = async () => {
  let fd = new FormData();
  fd.append("target", games.value[targetGameIdx.value].thumbnail);
  let file = await getFile(mode.value);

  fd.append("source", file);
  api
    .post("/api/single-games/score", fd, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    .then((res) => {
      per.value = res.data.toFixed(1);
      let elem = document.querySelector(".myBar");
      var width = 1;
      var id = setInterval(frame, 10);
      function frame() {
        if (width >= per.value) {
          clearInterval(id);
        } else {
          width++;
          elem.style.width = width + "%";
        }
      }
      if (per.value > 98) {
        alert("동일 인물입니다.");
        return;
      }
      if (
        rank.value.length < 20 ||
        rank.value[rank.value.length - 1].score < per.value
      ) {
        const reader = new FileReader();
        reader.onload = () => {
          store.dispatch("commonStore/setCurrentModalAsync", {
            name: "rankingRegist",
            data: {
              gameId: games.value[targetGameIdx.value].id,
              targetImage: games.value[targetGameIdx.value].thumbnail,
              image: fd.get("source"),
              userImage: reader.result,
              gameTitle: games.value[targetGameIdx.value].title,
              score: per.value,
            },
          });
        };
        reader.readAsDataURL(file);
      }
    })
    .catch((err) => {
      if (err.response.status === 400) {
        alert("사진에서 얼굴을 찾을 수 없습니다.");
      }
    });
};

const getFile = async (mode) => {
  let file;
  if (mode == "video") {
    let me = document.getElementById("videoElement");
    captureMode.value = true;
    setTimeout(() => {
      captureMode.value = false;
    }, 500);
    me.pause();
    const canvas = await html2canvas(me);
    let myImg = canvas.toDataURL("image/jpeg");
    let blob = dataURItoBlob(myImg);
    file = new File([blob], "img.jpeg");
    return file;
  } else {
    return uploadImage.value;
  }
};

const dataURItoBlob = (dataURI) => {
  let byteString = window.atob(dataURI.split(",")[1]);
  let mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];
  let ab = new ArrayBuffer(byteString.length);
  let ia = new Uint8Array(ab);
  for (let i = 0; i < byteString.length; i++) {
    ia[i] = byteString.charCodeAt(i);
  }

  let bb = new Blob([ab], { type: mimeString });
  return bb;
};

const openRankingImageModal = (image) => {
  store.dispatch("commonStore/setCurrentModalAsync", {
    name: "rankingImage",
    data: image,
  });
};

const fileUploadEvent = (e) => {
  uploadImage.value = e.target.files[0];
  console.log(uploadImage.value.name);
  if (uploadImage.value.name.split(".")[1] === "HEIF") {
    let blob = e.target.files[0];
    heic2any({ blob: blob, toType: "image/jpeg" }).then(function (resultBlob) {
      uploadImage.value = new File(
        [resultBlob],
        uploadImage.value.name.split(".")[0] + ".jpg",
        { type: "image/jpeg" }
      );
      console.log(uploadImage.value);
    });
  }
  mode.value = "image";
};

const convertFile = (file) => {
  //파일 미리보기
  return URL.createObjectURL(file);
};

const back = () => {
  isShow.value = true;
  uploadImage.value = null;
  mode.value = "";
  per.value = 0;
};

onMounted(() => {
  document.addEventListener("scroll", scrollHandler);
});

onBeforeUnmount(() => {
  document.removeEventListener("scroll", scrollHandler);
});
const scrollHandler = () => {
  const scrollTop = document.documentElement.scrollTop;
  const clientHeight = document.documentElement.clientHeight;
  const scrollHeight = document.documentElement.scrollHeight;
  const isAtTheBottom = scrollHeight === scrollTop + clientHeight;
  if (isAtTheBottom && !hasNext.value) {
    setTimeout(() => {
      moreList();
    }, 100);
  }
};

const moreList = () => {
  api
    .get(`/api/single-games`, {
      params: {
        keyword: pageConditionReq.value.keyword,
        page: pageConditionReq.value.page,
      },
    })
    .then((res) => {
      games.value.push(...res.data.content);
      hasNext.value = res.data.last;
      pageConditionReq.value.page++;
    })
    .catch((err) => {
      console.log(err);
    });
};

callApi();
</script>

<style scoped>
#view {
  display: flex;
  flex-direction: column;
  text-align: center;
}
#searchBar {
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  margin-bottom: 70px;
}

input {
  outline: none;
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
  border-top: 2px solid black;
  border-left: 2px solid black;
  border-bottom: 2px solid black;
  border-right: none;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  padding: 0 10px;
  height: 44px;
  width: calc(100%-100px);
}

#btn-s {
  margin-right: -10px;
  background-color: white;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
  border-top: 2px solid black;
  border-left: 2px solid black;
  border-bottom: 2px solid black;
  border-right: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  display: inline-block;
  height: 48px;
  background-color: #f08383;
  color: white;
}
#btn-s:hover {
  cursor: pointer;
}

.period-radius-on > div,
.search-radius-on > div {
  border-radius: 5px 5px 0 0;
}

.captureButton {
  top: 50%;
  background-color: #ffa6a6;
  color: #fff;
  border: none;
  border-radius: 10px;
  height: 30px;
  min-height: 30px;
  min-width: 110px;
  font-size: 20px;
  text-align: center;
  line-height: 0px;
  font-family: "NanumSquareRoundB";
}
.file-label {
  cursor: pointer;
  display: block;
  width: 50%;
}
.selectButton {
  pointer-events: none;
  top: 50%;
  background-color: #ffa6a6;
  color: #fff;
  border: none;
  border-radius: 10px;
  height: 30px;
  font-size: 20px;
  text-align: center;
  line-height: 0px;
  font-family: "NanumSquareRoundB";
  padding: 20px;
  margin: 1px;
  width: 100%;
}

.myProgress {
  width: 30vh;
  height: 20px;
  margin-top: 5px;
  margin-right: 5px;
  border-radius: 32px;
  background-color: #f1f1f1;
  display: flex;
  position: relative;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.12), 0 5px 5px rgba(0, 0, 0, 0.22);
}

.myBar {
  width: 0%;
  height: 20px;
  border-radius: 32px;
  background-color: #f87c7b;
}
.percent {
  position: relative;
  margin: auto;
}

#list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
  place-items: center;
}
#ifTotalPagesIsZero {
  top: 500px;
  left: 50%;
  transform: translate(-50%, 0);
  position: absolute;
  display: inline-block;
  text-align: center;
}
#ifTotalPagesIsZero p:first-child {
  font-size: 36px;
}
#nav {
  position: absolute;
  bottom: 70px;
  left: 50%;
  transform: translate(-50%, 0);
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}
#board {
  display: flex;
  margin-top: 30px;
  justify-content: center;
}
#imgElement,
#gameImage {
  width: 90%;
  height: 300px;
}
#buttonSection {
  display: flex;
  margin-top: 5px;
  justify-content: right;
}
#buttonList {
  width: 47%;
  display: flex;
  justify-content: space-around;
}
#etcSection {
  display: flex;
  margin-top: 10px;
  justify-content: right;
}

#info {
  position: absolute;
  font-size: 22px;
  top: 18%;
  left: 55%;
}

.backButton {
  top: 50%;
  background-color: #ffa6a6;
  color: #fff;
  border: none;
  border-radius: 10px;
  height: 30px;
  font-size: 20px;
  text-align: center;
  line-height: 0px;
  font-family: "NanumSquareRoundB";
  padding: 20px;
  margin: 1px;
  width: 90%;
  margin-bottom: 10px;
}
</style>
