<template>
  <div id="background">
    랭킹 등록
    <div id="joinForm">
      <ul class="join_box">
        <li class="checkBox check02">
          <ul class="clearfix">
            <li>이용약관 동의(필수)</li>
            <li class="checkBtn">
              <input type="checkbox" name="chk" />
            </li>
          </ul>
          <textarea name="" id="">니 얼굴 쓴다</textarea>
        </li>
      </ul>
      <ul class="footBtwrap clearfix">
        <li><button class="fpmgBt1" @click="closeModal">취소</button></li>
        <li><button class="fpmgBt2" @click="regist">등록</button></li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { computed } from "vue";
import { apiInstance } from "@/api/index";

const store = useStore();
const api = apiInstance();

const currentModal = computed(() => store.state.commonStore.currentModal);

const regist = () => {
  let fd = new FormData();
  fd.append("gameId", currentModal.value.data.gameId);
  fd.append("image", new File([currentModal.value.data.image], "img.jpeg"));
  fd.append("score", currentModal.value.data.score);
  api
    .post(`/api/ranks`, fd, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    .then(() => {
      closeModal();
    });
};

const closeModal = () => {
  store.dispatch("commonStore/setCurrentModalAsync", "");
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  background-color: #f7f7f7;
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
#background {
  background-color: white;
  padding: 5px;
}
img {
  margin: 2px;
  object-fit: cover;
}
#joinForm {
  width: 460px;
  margin: 0 auto;
}
ul.join_box {
  border: 1px solid #ddd;
  background-color: #fff;
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
  height: 90px;
  margin: 0 2%;
  background-color: #f7f7f7;
  color: #888;
  border: none;
}
.footBtwrap {
  margin-top: 15px;
}
.footBtwrap > li {
  float: left;
  width: 50%;
  height: 60px;
}
.footBtwrap > li > button {
  display: block;
  width: 100%;
  height: 100%;
  font-size: 20px;
  text-align: center;
  line-height: 60px;
}
.fpmgBt1 {
  background-color: #fff;
  color: #888;
}
.fpmgBt2 {
  background-color: lightsalmon;
  color: #fff;
}
</style>
