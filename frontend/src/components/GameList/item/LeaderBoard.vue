<template>
  <div class="bg">
    <!-- <div v-if="result.length" id="rankContainer"></div> -->
    <div id="title"><span>RANKING</span></div>
    <div id="ranking">
      <div v-for="(r, index) in props.rank" :key="r">
        <span v-if="index <= 2" class="badge">
          <img :src="require(`@/assets/images/medal${index + 1}.png`)" />
        </span>
        <span v-else class="number">{{ index + 1 }}</span>

        <span>
          {{ r.nickname }}
        </span>
        <span
          ><img
            :src="require(`@/assets/images/camera.png`)"
            @click="getImage(r.imageUrl)"
        /></span>
        <span>{{ r.score }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({ rank: Object });
const emit = defineEmits(["getImage"]);

const getImage = (image) => {
  emit("getImage", image);
};
</script>

<style scoped>
.bg {
  background-color: #f87c7b;
  width: 100%;
  height: 930px;
  border-radius: 20px;
  position: relative;
}
#title {
  text-align: center;
  margin: 55px 0;
}
#title > span {
  font-size: 80px;
  color: white;
  font-family: NanumSquareRoundEB;
}
#ranking {
  background-color: white;
  border-radius: 10px;
  width: 90%;
  height: 680px;
  position: absolute;
  left: 50%;
  transform: translate(-50%, 0);
  overflow-y: scroll;
}
#ranking > div {
  padding: 30px 50px;
  border-bottom: 1px solid #d9d9d9;
  display: flex;
  justify-content: space-between;
}
#ranking > div:last-child {
  border-bottom: none;
}
#ranking > div > span {
  font-size: 36px;
  display: inline-block;
}
.badge {
  width: 50px;
  height: 40px;
  text-align: center;
}
.badge img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.number {
  color: #717171;
  /* border: 1px solid red; */
  width: 50px;
  text-align: center;
}
#ranking > div > span:nth-child(2) {
  /* border: 1px solid yellow; */
  width: 400px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: center;
}

#ranking > div > span:nth-child(4) {
  color: #ffb800;
  /* border: 1px solid blue; */
  width: 80px;
  text-align: center;
}
#ranking::-webkit-scrollbar {
  width: 15px;
}
#ranking::-webkit-scrollbar-thumb {
  background-color: #d9d9d9;
  border-radius: 10px;
  background-clip: padding-box;
  border: 4px solid transparent;
}
#ranking::-webkit-scrollbar-track {
  background-color: white;
  border-radius: 10px;
  box-shadow: inset 0px 0px 2px white;
}
</style>
