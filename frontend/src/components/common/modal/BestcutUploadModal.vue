<template>
  <div>
    <div id="reportModal">
      <div id="myImageSection">
        <div v-for="(image, index) in resultImages" :key="index">
          <img :src="image" @click="updateSelectedIndex(index)" />
          <span
            @click="
              check(index);
              updateSelectedIndex(index);
            "
            :class="{
              isChecked: isChecked[index] === true,
              isNotChecked: isChecked[index] === false,
            }"
          ></span>
        </div>
        <!-- <div v-for="n in 4" :key="n">
          <img :src="require(`@/assets/images/minus.png`)" />
        </div> -->
      </div>
      <div id="uploadSection">
        <div id="ImageCompareSection">
          <img :src="resultImages[selectedIndex]" />
          <img
            :src="`${IMAGE_PATH}/${room.gameImages[selectedIndex].gameImage}`"
          />
        </div>
        <div id="submitSection">
          <div id="bestCutTitleSection">
            <label for="bestcutTitle">{{ selectedIndex + 1 }}번째 컷</label>
            <input
              type="text"
              id="bestcutTitle"
              v-model="inputs[selectedIndex]"
              placeholder="베스트컷 제목을 입력해 주세요."
            />
          </div>
          <div id="uploadButtonSection">
            <span>총 {{ isCheckedCount }} 장</span>
            <button @click="upload">업로드</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { ref } from "vue";
import { computed } from "vue";
import { useStore } from "vuex";
import process from "process";

const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;
const store = useStore();
const currentModal = computed(() => store.state.commonStore.currentModal);
const resultImages = ref(currentModal.value.data[0]);
const streamManager = ref(currentModal.value.data[1]);
const room = ref(currentModal.value.data[2]);
const api = apiInstance();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const bestcutSaveReq = ref({
  pinNumber: undefined,
  socketId: undefined,
  gameTitle: undefined,
  images: [],
});
const isChecked = ref([
  false,
  false,
  false,
  false,
  false,
  false,
  false,
  false,
  false,
  false,
]);
const isCheckedCount = ref(0);
const check = (index) => {
  if (isChecked.value[index]) {
    //원래 true였으면
    isCheckedCount.value--;
  } else {
    //원래 false였으면
    isCheckedCount.value++;
  }
  isChecked.value[index] = !isChecked.value[index];
};
const inputs = ref(["", "", "", "", "", "", "", "", "", ""]);
const selectedIndex = ref(0);
const updateSelectedIndex = (index) => {
  selectedIndex.value = index;
};

const upload = () => {
  //유효성 검사
  if (isCheckedCount.value === 0) {
    alert("최소 1장의 베스트컷을 업로드 해주세요.");
    return;
  }
  for (let i = 0; i < 10; i++) {
    if (isChecked.value[i] === true && inputs.value[i].length === 0) {
      alert(i + 1 + "번째 베스트컷의 제목을 입력해 주세요");
      return;
    }
  }

  bestcutSaveReq.value.pinNumber = streamManager.value.session.sessionId;
  bestcutSaveReq.value.socketId =
    streamManager.value.session.connection.connectionId;
  bestcutSaveReq.value.gameTitle = room.value.gameTitle;
  isChecked.value.forEach((element, index) => {
    if (element) {
      bestcutSaveReq.value.images.push({
        bestcutIndex: index,
        bestcutImgTitle: inputs.value[index],
        gameImgUrl: room.value.gameImages[index].gameImage,
        gameImgDesc: room.value.gameImages[index].gameImageDesc,
      });
    }
  });
  api
    .post(`/api/bestcuts`, bestcutSaveReq.value, {
      headers: {
        "access-token": accessToken,
      },
    })
    .then(() => {
      bestcutSaveReq.value.images = [];
      isChecked.value = [
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
      ];
      isCheckedCount.value = 0;

      alert("업로드가 완료 되었습니다.");
    })
    .catch(() => {
      alert("업로드에 실패 하였습니다.");
    });
};
</script>

<style scoped>
#reportModal {
  background-color: white;
  width: 1130px;
  height: 660px;
  border-radius: 10px;
  padding: 5px 0;
  display: flex;
}
#myImageSection {
  /* border: 1px solid red; */
  margin: 10px;
  width: 250px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  height: 640px;
  align-content: space-between;
}
#myImageSection > div {
  width: 120px;
  height: 120px;
  position: relative;
}
#myImageSection img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
#myImageSection img:hover {
  cursor: pointer;
}
#myImageSection span {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 20px;
  height: 20px;
  border-radius: 3px;
  border: 1px solid black;
}
#myImageSection span:hover {
  cursor: pointer;
}
.isNotChecked {
  background-color: white;
}
.isChecked {
  background-color: #ff6969;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("@/assets/images/checkbox.png");
}

#uploadSection {
  margin: 10px;
  /* border: 1px solid blue; */
  width: 830px;
  height: 640px;
}
#ImageCompareSection {
  display: flex;
  justify-content: space-between;
}

#uploadSection img {
  width: 400px;
  height: 400px;
  object-fit: cover;
  /* border: 1px solid salmon; */
}
#submitSection {
  position: relative;
  width: 100%;
  height: 240px;
}
#bestCutTitleSection {
  position: absolute;
  top: 40px;
  left: 15px;
}
#bestCutTitleSection > * {
  margin: 0 5px;
}
#uploadButtonSection {
  position: absolute;
  bottom: 15px;
  right: 15px;
}
#uploadButtonSection > * {
  margin: 0 10px;
}

label {
  font-size: 20px;
}
input[type="text"] {
  outline: none;
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  padding: 0 10px;
  height: 44px;
  width: 500px;
}
#uploadButtonSection span {
  font-size: 20px;
}
#uploadButtonSection button {
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  height: 48px;
  background-color: #f08383;
  color: white;
  width: 120px;
}
#uploadButtonSection button:hover {
  cursor: pointer;
}
</style>
