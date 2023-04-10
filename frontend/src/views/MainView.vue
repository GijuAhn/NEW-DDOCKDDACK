<template>
  <div id="view">
    <div class="box">
      <div class="logo"><span>똑딱</span></div>
      <div class="form">
        <input
          @keyup.enter="join(pinNumber)"
          type="text"
          v-model.trim="pinNumber"
          class="input"
          placeholder="게임 PIN"
          maxlength="6"
        />
        <div class="button" @click="join(pinNumber)">입력</div>
      </div>
    </div>
    <div class="cirle">
      <div class="cirle1"></div>
      <div class="cirle2"></div>
      <div class="cirle3"></div>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import router from "@/router/index.js";

const store = useStore();

const join = (pinNumber) => {
  router.replace(`/gameroom/${pinNumber}`);
};
store.dispatch("commonStore/setColorAsync", "default");
</script>

<style scoped>
#view {
  background-color: #fdf8ec;
  height: calc(100vh - 95px + 1000px);
  position: relative;
  overflow: hidden;
}
.box {
  position: absolute;
  left: 50%;
  top: calc((100vh - 95px) * 0.5);
  transform: translate(-50%, -50%);
}

.logo {
  font-family: "Gugi-Regular";
  font-size: 96px;
  text-align: center;
}

.form {
  width: 300px;
  font-size: 20px;
  background-color: #f9cf5c;
  border-radius: 10px;
  height: 200px;
  margin: 0 auto;
  text-align: center;
}

.input,
.button {
  border-radius: 10px;
  margin: 0 auto;
}

.input {
  background-color: white;
  width: 220px;
  height: 50px;
  text-align: center;
  position: relative;
  top: 45px;
  border: none;
  font-size: 20px;
  outline: none;
  padding: 0;
  font-family: "NanumSquareRoundB";
}

.button {
  background-color: #333333;
  color: white;
  width: 220px;
  height: 50px;
  line-height: 50px;
  text-align: center;
  position: relative;
  top: 55px;
}
.button:hover {
  cursor: pointer;
}

.cirle1 {
  width: 800px;
  height: 800px;
  border-radius: 50%;
  background-color: #f87c7b;
  position: absolute;
  top: -350px;
  right: -250px;
}
.cirle2 {
  width: 800px;
  height: 800px;
  border-radius: 50%;
  background-color: #77a4cc;
  position: absolute;
  top: 140px;
  left: -250px;
}

.cirle3 {
  width: 800px;
  height: 800px;
  border-radius: 50%;
  background-color: #f4e6ca;
  position: absolute;
  top: 700px;
  right: 70px;
}
</style>
