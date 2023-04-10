<template>
  <div class="white-bg">
    <div v-if="result.length" id="rankContainer">
      <div id="box1">
        <div>
          <span>{{ rankTitle[1] }}</span>
        </div>
        <div v-if="result[1]">
          <img id="rank1" :src="`data:image/jpeg;base64, ${result[1].image}`" />
        </div>
        <div v-if="result[1]">
          <span>{{ result[1].nickname }}</span>
        </div>
      </div>
      <div id="box0">
        <div>
          <span>{{ rankTitle[0] }}</span>
        </div>
        <div v-if="result[0]">
          <img id="rank0" :src="`data:image/jpeg;base64, ${result[0].image}`" />
        </div>
        <div v-if="result[0]">
          <span>{{ result[0].nickname }}</span>
        </div>
      </div>
      <div id="box2">
        <div>
          <span>{{ rankTitle[2] }}</span>
        </div>
        <div v-if="result[2]">
          <img id="rank2" :src="`data:image/jpeg;base64, ${result[2].image}`" />
        </div>
        <div v-if="result[2]">
          <span>{{ result[2].nickname }}</span>
        </div>
      </div>
    </div>

    <div v-else>
      <div class="loader">
        <div class="circle" id="a"></div>
        <div class="circle" id="b"></div>
        <div class="circle" id="c"></div>
      </div>
      <div class="caption"><span>채점 중입니다...</span></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { computed } from "vue";
import { useStore } from "vuex";

const store = useStore();
const currentModal = computed(() => store.state.commonStore.currentModal);
// const round = ref(currentModal.value.data[0]);
const result = ref(currentModal.value.data[1]);
// const isEnd = ref(currentModal.value.data[2]);
// const winner = ref(currentModal.value.data[3]);
// const publisher = ref(currentModal.value.data[4]);

const rankTitle = ["1st", "2nd", "3rd"];
</script>

<style scoped>
.white-bg {
  background-color: #fdf8ec;
  width: 1300px;
  height: 750px;
  border-radius: 10px;
  position: relative;
}
.loader {
  height: 70px;
  display: flex;
  width: 510px;
  /* border: 1px solid yellow; */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  position: absolute;
}
.circle {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  margin: 0 45px;
  animation: jump 1s linear infinite;
}
.circle:nth-child(1) {
  background: #f87c7b;
}
.circle:nth-child(2) {
  background: #f9cf5c;
}
.circle:nth-child(3) {
  background: #77a4cc;
}
.caption {
  font-size: 40px;
  color: black;
  top: 70%;
  left: 50%;
  transform: translate(-50%, -70%);
  position: absolute;
  font-family: NanumSquareRoundEB;
}
@keyframes jump {
  0% {
    margin-top: 0;
  }
  35% {
    margin-top: -75px;
  }
  70% {
    margin-top: 0px;
  }
}

#b {
  animation-delay: 0.2s;
}
#c {
  animation-delay: 0.4s;
}

#rankContainer {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  height: 750px;
}
#rankContainer > div {
  width: 400px;
  height: 700px;
  border: 2px solid black;
  position: relative;
  box-shadow: 0 0 10px #8b8b8b;
}
#box0 {
  background-color: #fbd600;
}
#box1 {
  background-color: #c1c1c1;
}
#box2 {
  background-color: #f3b251;
}
#rankContainer > div > * {
  left: 50%;
  transform: translateX(-50%);
  position: absolute;
}
#rankContainer > div > div:nth-child(1) {
  top: 50px;
}
#rankContainer > div > div:nth-child(1) > span {
  font-family: Gugi-Regular;
  text-shadow: -2px 0 #000, 0 2px #000, 2px 0 #000, 0 -2px #000;
  color: white;
  font-size: 80px;
}
#rankContainer > div > div:nth-child(2) {
  top: 170px;
}
#rankContainer > div > div:nth-child(3) {
  top: 585px;
}
#rankContainer > div > div:nth-child(3) > span {
  font-size: 24px;
  border: 2px solid black;
  background-color: white;
  width: 200px;
  height: 50px;
  display: inline-block;
  border-radius: 50px;
  text-align: center;
  line-height: 50px;
}

img {
  width: 350px;
  height: 350px;
  object-fit: cover;
  border: 2px solid black;
}
</style>
