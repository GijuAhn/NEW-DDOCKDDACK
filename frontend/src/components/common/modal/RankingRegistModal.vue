<template>
  <div id="background">
    <div
      class="title"
      style="display: flex; justify-content: center; align-items: center"
    >
      <img :src="require(`@/assets/images/medal1.png`)" width="50" />
      TOP 20에 진입하셨습니다!
      <img :src="require(`@/assets/images/medal1.png`)" width="50" />
    </div>
    <div class="img-box">
      <img
        class="img"
        :src="IMAGE_PATH + '/' + currentModal.data.targetImage"
      />
      <img class="img" :src="currentModal.data.userImage" />
    </div>
    <div class="title" style="margin-top: 40px; font-size: 25px">
      {{ currentModal.data.gameTitle }}
    </div>
    <div style="display: flex; justify-content: center">
      <div class="myProgress">
        <div class="myBar-modal"></div>
        <div class="percent">{{ per }}%</div>
      </div>
    </div>
    <div class="title" style="font-size: 20px; margin-top: 60px">
      약관에 동의하면 명예의 전당에 사진을 등록할 수 있어요!
    </div>
    <div id="joinForm">
      <ul class="join_box">
        <li class="checkBox check02">
          <ul class="clearfix">
            <li @click="agreementVisible = !agreementVisible">
              초상권 수집·이용 동의(필수)
            </li>
            <li class="checkBtn">
              <input type="checkbox" name="chk" v-model="agreement" />
            </li>
          </ul>
          <textarea v-if="agreementVisible">
□초상권의 수집 및 사용목적
수집된 초상권은 랭킹 서비스에 이용되어 제3자가 열람이 가능합니다.

□초상권의 보유 및 이용기간
수집일로부터 1년간 보유 및 이용되며 해당 기간이 경과한 경우 즉시 파기합니다.

□초상권 사용에 대한 거부 권리
초상권 사용에 대한 동의를 거부할 수 있으며, 동의를 거부할 경우 랭킹 등록 서비스 이용 불가합니다.

□저작물에 대한 소유권 및 저작권
해당 초상에 대한 소유권 및 저작권이 똑딱에 있음을 알립니다.
          </textarea>
        </li>
      </ul>
      <ul class="footBtwrap clearfix">
        <li>
          <button class="fpmgBt fpmgBt1" @click="closeModal">취소</button>
        </li>
        <li>
          <button
            class="fpmgBt fpmgBt2"
            @click="regist"
            v-bind:disabled="!agreement"
          >
            등록
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { computed, onMounted, ref, watch } from "vue";
import { apiInstance } from "@/api/index";

const store = useStore();
const api = apiInstance();
const currentModal = computed(() => store.state.commonStore.currentModal);
const accessToken = computed(() => store.state.memberStore.accessToken);

const agreement = ref(false);
const per = ref(0);
const agreementVisible = ref(false);
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

watch(
  () => per.value,
  (newScore) => {
    if (newScore !== currentModal.value.data.score) return;

    per.value = newScore;
    let elem = document.querySelector(".myBar-modal");
    let width = 1;
    let id = setInterval(frame, 10);
    function frame() {
      if (width >= per.value) {
        clearInterval(id);
      } else {
        width++;
        elem.style.width = width + "%";
      }
    }
  }
);

onMounted(() => {
  per.value = currentModal.value.data.score;
});

const regist = () => {
  let fd = new FormData();
  fd.append("gameId", currentModal.value.data.gameId);
  fd.append("image", new File([currentModal.value.data.image], "img.jpeg"));
  fd.append("score", currentModal.value.data.score);
  api
    .post(`/api/ranks`, fd, {
      headers: {
        "Content-Type": "multipart/form-data",
        "access-token": accessToken.value,
      },
    })
    .then(() => {
      closeModal();
      window.location.reload();
    })
    .catch((err) => {
      err;
      alert("로그인이 필요한 기능입니다.");
    });
};

const closeModal = async () => {
  store.dispatch("commonStore/setCurrentModalAsync", "");
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
ul > li {
  list-style: none;
}
a {
  text-decoration: none;
}
.clearfix::after {
  content: "";
  display: block;
  clear: both;
}
.title {
  font-size: 30px;
  text-align: center;
  margin-bottom: 20px;
}
#background {
  background-color: white;
  padding: 40px;
  width: 800px;
  border-radius: 20px;
}
img {
  margin: 2px;
  object-fit: cover;
}
#joinForm {
  width: 100%;
  /* height: 300px; */
  margin: 0px auto;
}
ul.join_box {
  border: 1px solid #ddd;
  background-color: #fff;
  cursor: pointer;
  widows: 94%;
  margin: 0% 3%;
}
.checkBox,
.checkBox > ul {
  position: relative;
}
.checkBox > ul > li {
  float: left;
}
.checkBox > ul > li:first-child {
  width: 85%;
  padding: 15px;
  font-weight: 600;
  color: #888;
}
.checkBox > ul > li:nth-child(2) {
  position: absolute;
  top: 50%;
  right: 30px;
  margin-top: -12px;
}
.checkBox textarea {
  width: 96%;
  height: 190px;
  margin: 0 2%;
  background-color: #f7f7f7;
  color: #888;
  border: none;
}
.footBtwrap {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
}
.footBtwrap > li {
  /* float: right; */
  display: flex;
  justify-content: center;
  align-items: center;
  width: 50%;
  height: 60px;
}
.footBtwrap > li > button {
  display: block;
  /* width: 100%; */
  height: 100%;
  font-size: 20px;
  text-align: center;
  line-height: 60px;
}

.fpmgBt {
  width: 90%;
  border: none;
  border-radius: 10px;
  height: 30px;
  font-size: 20px;
  text-align: center;
  line-height: 0px;
  font-family: "NanumSquareRoundB";
  /* margin-inline: ; */
}

.fpmgBt1 {
  background-color: #ffeec5;
  color: #888;
}

.fpmgBt1:hover {
  background-color: #ffcb50;
  color: #666666;
}

.fpmgBt2 {
  background-color: #ffa6a67a;
  color: #242222;
}
.fpmgBt2:hover {
  background-color: #ffa6a6;
  color: #242222;
}

.fpmgBt2:disabled,
.fpmgBt2[disabled] {
  /* border: 1px solid #999999; */
  background-color: #cccccc;
  color: #666666;
}

.img-box {
  display: flex;
  width: 100%;
  max-height: 300px;
  overflow: hidden;
}
.img {
  height: 100%;
  width: 50%;
}

.myProgress {
  width: 80%;
  height: 30px;
  margin-top: 0px;
  margin-right: 5px;
  border-radius: 32px;
  background-color: #f1f1f1;
  display: flex;
  position: relative;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.12), 0 5px 5px rgba(0, 0, 0, 0.22);
}

.myBar-modal {
  width: 0%;
  height: 30px;
  border-radius: 32px;
  background-color: #f87c7b;
}
.percent {
  position: absolute;
  font-size: 15px;
  left: 50%;
  top: 7px;
}
</style>
