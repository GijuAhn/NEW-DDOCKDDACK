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

    <div id="container" v-show="!isShow">
      <img
        id="gameImage"
        :src="`${IMAGE_PATH}/${games[targetGameIdx].thumbnail}`"
      />
      <video
        autoplay="true"
        id="videoElement"
        :class="{ blinking: captureMode }"
      ></video>
    </div>
    <div id="etcSection" v-if="rank">
      <div class="myProgress">
        <div class="myBar"></div>
        <div class="percent">{{ per }}%</div>
      </div>
      <div>
        <button @click="capture" class="captureButton">
          <img
            :src="require(`@/assets/images/camera-button.png`)"
            @click="getImage(r.imageUrl)"
          />
        </button>
      </div>
    </div>

    <div id="board" v-if="rank">
      <leader-board
        :rank="rank"
        @getImage="(image) => openRankingImageModal(image)"
      />
    </div>

    <page-nav
      :totalPageCount="totalPages"
      :value="pageConditionReq.page"
      @change="(num) => changePage(num)"
      v-if="isShow"
    ></page-nav>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useStore } from "vuex";
import SingleGame from "@/components/GameList/item/SingleGame.vue";
import LeaderBoard from "@/components/GameList/item/LeaderBoard.vue";
import html2canvas from "html2canvas";

import PageNav from "@/components/common/PageNav.vue";

const api = apiInstance();
const store = useStore();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const games = ref();
const rank = ref();
const isShow = ref(true);
const captureMode = ref(false);
const targetGameIdx = ref(0);
const per = ref(0);
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

let totalPages = ref();

onMounted(() => {});

onBeforeUnmount(() => {
  const video = document.getElementById("videoElement");
  if (video) {
    if (video.srcObject) {
      video.srcObject.getTracks().forEach((track) => {
        track.stop();
      });
    }
  }
});

//페이징 이동
const changePage = (page) => {
  pageConditionReq.value.page = page;
  callApi();
};

let pageConditionReq = ref({
  keyword: "",
  page: 1,
});

const ready = (value) => {
  targetGameIdx.value = value.index;
  isShow.value = false;
  const video = document.getElementById("videoElement");
  if (navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices
      .getUserMedia({ video: true, audio: false })
      .then(function (stream) {
        video.srcObject = stream;
      })
      .catch((err) => {
        console.log(err);
      });
  }
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

const capture = () => {
  let me = document.getElementById("videoElement");
  captureMode.value = true;
  setTimeout(() => {
    captureMode.value = false;
  }, 500);
  me.pause();
  html2canvas(me)
    .then((canvas) => {
      let myImg = canvas.toDataURL("image/jpeg");
      let blob = dataURItoBlob(myImg);
      let fd = new FormData();
      fd.append("source", new File([blob], "img.jpeg"));
      fd.append("target", games.value[targetGameIdx.value].thumbnail);
      api
        .post(`/api/single-games/score`, fd, {
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
          if (
            rank.value.length < 20 ||
            rank.value[rank.value.length - 1].score < per.value
          ) {
            store.dispatch("commonStore/setCurrentModalAsync", {
              name: "rankingRegist",
              data: {
                gameId: games.value[targetGameIdx.value].id,
                image: blob,
                score: per.value,
              },
            });
          }
        });
    })
    .catch((err) => {
      console.log(err);
    });
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

callApi();
</script>

<style scoped>
#view {
  border: 2px solid black;
  width: 1060px;
  position: relative;
  top: -320px;
  left: 50%;
  transform: translate(-50%, 0);
  background-color: white;
  padding: 70px;
  height: 1460px;
}
#searchBar {
  display: flex;
  flex-wrap: wrap;
  flex-direction: row; /*수평 정렬*/
  align-items: center;
  justify-content: center;
  margin-bottom: 70px;
}
#searchBar > div {
  margin: auto;
}
#btn-p {
  margin-left: -10px;
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
  border-top: 2px solid black;
  border-left: 2px solid black;
  border-bottom: 2px solid black;
  border-right: none;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  display: inline-block;
  height: 48px;
  width: 99px;
}
#btn-p:hover {
  cursor: pointer;
}
#btn-r {
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
  width: 101px;
}
#btn-r:hover {
  cursor: pointer;
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
  width: 365px;
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
  width: 80px;
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

.captureButton:hover {
  background-color: #f87c7b;
  transition: 0.7s;
  cursor: pointer;
}

.myProgress {
  width: 380px;
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
  position: absolute;
  left: 165px;
}

#list {
  display: grid;
  gap: 35px 0;
  grid-template-columns: repeat(3, 1fr);
  width: 1090px;
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

#container {
  display: flex;
  gap: 35px 0;
  grid-template-columns: repeat(3, 1fr);
  justify-content: space-between;
}
#board {
  display: flex;
  margin-top: 70px;
  justify-content: center;
}

#gameImage {
  width: 500px;
  height: 460px;
}
#etcSection {
  display: flex;
  margin-top: 10px;
  justify-content: right;
}

#videoElement {
  width: 500px;
  height: 460px;
  background-color: #666;
  object-fit: fill;
}
.blinking {
  animation: blink 0.5s ease-in-out infinite alternate;
}
@keyframes blink {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>
