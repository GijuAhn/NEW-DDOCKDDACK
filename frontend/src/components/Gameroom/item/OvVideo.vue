<template>
  <div id="parent">
    <video autoplay ref="video" id="myVideo1" />
  </div>
</template>

<script setup>
import { defineProps, onMounted, ref } from "vue";

const props = defineProps({ streamManager: Object });
const video = ref(null);
onMounted(() => {
  props.streamManager.addVideoElement(video.value);
});
</script>

<style scoped>
#parent {
  /* border: 1px solid red; */
  width: 100%;
  height: 100%;
  position: relative;
}
#myVideo1 {
  border-radius: 10px;
  border: 1px solid #464646;
  /* box-shadow: 0 0 0 1px aqua inset; */
  /* visibility: hidden; */

  /* width: 150px;
  height: 150px; */

  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  height: calc(100% - 20px);
  width: calc(100% - 20px);
  margin: 10px;
  object-fit: contain;
}
</style>
