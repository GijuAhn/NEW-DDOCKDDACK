<template>
  <div id="view">
    <div id="stateIsZero" v-show="step === 0">
      <form>
        <div>
          <div class="label">
            <label for="gameTitle" class="input-name">제목</label>
          </div>
          <div>
            <input
              id="gameTitle"
              type="text"
              v-model="gameSaveReq.gameTitle"
              placeholder="게임 제목을 입력해 주세요."
            />
          </div>
        </div>
        <div>
          <div class="label">
            <label for="gameDesc" class="input-name">설명</label>
          </div>
          <div>
            <input
              id="gameDesc"
              type="text"
              v-model="gameSaveReq.gameDesc"
              placeholder="게임 설명을 입력하세요."
            />
          </div>
        </div>
        <div>
          <div class="label">
            <label class="input-name">이미지 업로드</label>
          </div>
          <input
            type="file"
            @change="fileUploadEvent"
            accept=".jpg,.jpeg"
            multiple
            id="fileInput"
            style="display: none"
          />

          <div id="uploadSection">
            <div id="uploadDescSection">
              <label
                for="fileInput"
                class="file-label"
                @dragover="dragover"
                @drop="drop"
              >
                <span v-if="gameSaveReq.images.length === 0"
                  ><span
                    >이곳을 클릭하거나 파일을 드래그하여 업로드 하세요.</span
                  ><br /><span>&#40;최소 10장, 최대 20장&#41;</span></span
                >
                <span v-else> </span
              ></label>
            </div>
            <div id="previewSection">
              <img
                v-for="item in gameSaveReq.images"
                :key="item"
                :src="convertFile(item.gameImage)"
                alt="이미지 미리보기..."
                width="180"
                height="180"
              />
            </div>
          </div>
        </div>
      </form>

      <div class="move-btn">
        <button @click="changeStep" class="yellow">다음</button>
      </div>
    </div>
    <div id="stateIsOne" v-show="step === 1">
      <div>
        <div class="label"><label class="input-name">사진 설명</label></div>
        <div id="descSection">
          <div v-for="item in gameSaveReq.images" :key="item">
            <img
              :src="convertFile(item.gameImage)"
              alt="이미지 미리보기..."
              width="120"
              height="120"
            />
            <input
              id="title"
              type="text"
              v-model="item.gameImageDesc"
              placeholder="설명을 입력하세요."
            />
            <img
              :src="require(`@/assets/images/minus.png`)"
              width="40"
              height="40"
              @click="removeLine(item)"
              class="minus"
            />
          </div>
        </div>
      </div>
      <div class="move-btn">
        <button @click="changeStep" class="gray">이전</button>
        <button @click="submit" class="yellow">완료</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { apiInstance } from "@/api/index";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const store = useStore();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const router = useRouter();
const api = apiInstance();

const step = ref(0); // 0 : 이전, 1: 다음

const changeStep = () => {
  //이전<->다음
  step.value = (step.value + 1) % 2;
};

const gameSaveReq = ref({
  gameTitle: "",
  gameCategory: "PICTURE",
  memberId: 1, //현재 로그인 유저 데이터 필요
  gameDesc: "",
  images: [],
});

const fileUploadEvent = (e) => {
  storeImage(e.target.files);
};

const storeImage = (f) => {
  if (f) {
    const files = Array.from(f);
    if (files.length + gameSaveReq.value.images.length > 20) {
      alert("최대 20장의 이미지만 등록 가능합니다!");
      return;
    }
    let wrongFileCount = 0;
    for (const file of files) {
      let fileDot = file.name.lastIndexOf(".");
      let fileType = file.name.substring(fileDot + 1, file.name.length);
      if (fileType !== "jpg" && fileType !== "jpeg") {
        wrongFileCount++;
        continue;
      }
      if (file.size >= 2097152) {
        wrongFileCount++;
        continue;
      }

      gameSaveReq.value.images.push({
        gameImage: file,
        gameImageDesc: "",
      });
    }
    if (wrongFileCount !== 0) {
      alert(
        "2MB 이하의 jpg/jpeg 형식의 이미지 파일만 업로드 가능합니다. " +
          wrongFileCount +
          "개의 이미지가 제외되었습니다."
      );
    }
  }
};

const convertFile = (file) => {
  //파일 미리보기
  return URL.createObjectURL(file);
};

const removeLine = (item) => {
  //images의 한 줄 삭제
  const index = gameSaveReq.value.images.indexOf(item);
  gameSaveReq.value.images.splice(index, 1);
};

const submit = () => {
  //완료버튼 클릭

  //유효성 검사
  let error = false;
  if (!gameSaveReq.value.gameTitle) {
    error = true;
    alert("게임 제목을 입력해 주세요.");
    return;
  }
  if (gameSaveReq.value.gameTitle.length > 30) {
    error = true;
    alert("게임 제목은 최대 30자 까지 입력해 주세요.");
    return;
  }
  if (gameSaveReq.value.gameDesc.length > 50) {
    error = true;
    alert("게임 설명은 최대 50자 까지 작성해 주세요.");
    return;
  }
  if (
    gameSaveReq.value.images.length < 10 ||
    gameSaveReq.value.images.length > 20
  ) {
    error = true;
    alert("사진은 최소 10장, 최대 20장 등록해 주세요.");
    return;
  }
  for (let i = 0; i < gameSaveReq.value.images.length; i++) {
    if (!gameSaveReq.value.images[i].gameImage) {
      error = true;
      alert("이미지를 등록해 주세요");
      return;
    } else if (gameSaveReq.value.images[i].gameImageDesc.length > 50) {
      error = true;
      alert("이미지 설명은 최대 50자 까지 작성해 주세요.");
      return;
    }
  }
  if (!error) createGame();
};

const createGame = () => {
  //FormData에 담아 axios.post
  const formData = new FormData();
  formData.append("gameTitle", gameSaveReq.value.gameTitle);
  formData.append("gameCategory", gameSaveReq.value.gameCategory);
  formData.append("memberId", gameSaveReq.value.memberId);
  formData.append("gameDesc", gameSaveReq.value.gameDesc);
  for (let i = 0; i < gameSaveReq.value.images.length; i++) {
    formData.append(
      "images[" + i + "].gameImage",
      gameSaveReq.value.images[i].gameImage
    );
    formData.append(
      "images[" + i + "].gameImageDesc",
      gameSaveReq.value.images[i].gameImageDesc
    );
  }

  api
    .post(`/api/games`, formData, { headers: { "access-token": accessToken } })
    .then(() => {
      router.push({ path: "/member/myGame" });
    })
    .catch((error) => {
      console.log(error);
    });
};
const dragover = (e) => {
  e.preventDefault();
};
const drop = (e) => {
  e.preventDefault();
  storeImage(e.dataTransfer.files);
};
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
}
form > div {
  margin-bottom: 40px;
}
.label {
  margin-bottom: 3px;
}
label {
  font-size: 20px;
}
#stateIsZero input[type="text"] {
  outline: none;
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  padding: 0 10px;
  height: 44px;
  width: 1040px;
}
#stateIsOne input[type="text"] {
  outline: none;
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  padding: 0 10px;
  height: 44px;
  width: 680px;
}
#uploadSection {
  border-radius: 5px;
  border: 2px solid black;
  width: 100%;
  height: 820px;
  position: relative;
}
#uploadDescSection {
  /* border: 1px solid red; */
  width: 100%;
  height: 100%;
  position: absolute;
}
#uploadDescSection label:hover {
  cursor: pointer;
}
#uploadDescSection label {
  /* border: 1px solid blue; */
  width: 100%;
  height: 100%;
  position: absolute;
}
#uploadDescSection label > span {
  text-align: center;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 780px;
}
#uploadDescSection label > span > span:first-child {
  font-size: 36px;
}
#uploadDescSection label > span > span:last-child {
  font-size: 24px;
}
#previewSection {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: repeat(4, 1fr);
  grid-template-columns: repeat(5, 1fr);
  place-items: center;
}

#descSection {
  border-radius: 5px;
  border: 2px solid black;
  width: 1000px;
  padding: 30px;
  margin-bottom: 40px;
}
#descSection > div {
  padding-right: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0 0 40px 0;
}

#descSection > div:last-child {
  margin: 0 !important;
}

/* #descSection > div > * {
  margin: auto;
} */
.move-btn {
  text-align: right;
}
.yellow {
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  height: 48px;
  background-color: #ffb800;
  color: white;
  width: 120px;
  margin: 20px 0 0 0;
}
.yellow:hover {
  cursor: pointer;
}
.gray {
  background-color: white;
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  height: 48px;
  background-color: #515151;
  color: white;
  width: 120px;
  margin: 20px 50px 0 0;
}
.gray:hover {
  cursor: pointer;
}
img {
  object-fit: cover;
  border-radius: 5px;
}
.minus:hover {
  cursor: pointer;
}
</style>
