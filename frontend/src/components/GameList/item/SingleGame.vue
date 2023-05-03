<template>
  <div id="content">
    <div id="topSection">
      <img
        :src="`${IMAGE_PATH}/${props.game.thumbnail}`"
        alt="대표사진"
        class="image"
      />
    </div>
    <div id="bottomSection">
      <div id="gameTitle">
        <span>{{ props.game.title }}</span>
      </div>

      <div id="createRoomButton">
        <button @click="ready()">
          <span class="play-btn"></span>
          &nbsp;랭킹 도전
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import process from "process";

const emit = defineEmits(["ready"]);
const props = defineProps(["game", "index"]);
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

const ready = () => {
  emit("ready", { index: props.index });
};
</script>

<style scoped>
#content {
  border: 2px solid black;
  width: 325px;
  height: 380px;
}
#createRoomButton {
  margin-top: 10px;
}
#gameTitle {
  width: 95%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
#createRoomButton button {
  border: 1px solid #da6c6b;
  background-color: white;
  font-size: 14px;
  font-family: "NanumSquareRoundB";
  width: 100%;
  padding: 10px 0;

  color: #da6c6b;
}
#createRoomButton button:hover {
  color: white;
  background-color: #da6c6b;
  cursor: pointer;
  transition: 0.3s;
}
#createRoomButton button:hover .play-btn {
  background-image: url("@/assets/images/play.png");
}
.play-btn {
  display: inline-block;
  background-size: contain;
  background-repeat: no-repeat;
  width: 12px;
  height: 12px;
  vertical-align: middle;
  background-image: url("@/assets/images/play2.png");
}
#gameTitle span {
  font-size: 22px;
}
#topSection {
  position: relative;
}
.image {
  width: 325px;
  height: 290px;
  object-fit: cover;
  display: inline-block;
  /* display: none; */
}
#bottomSection {
  padding: 5px 10px;
  position: relative;
}
#etcButton {
  position: absolute;
  top: 0px;
  left: 290px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  text-align: center;
}
#etcButton:hover {
  background-color: #d9d9d9;
  cursor: pointer;
  transition: 0.3s;
}
</style>
