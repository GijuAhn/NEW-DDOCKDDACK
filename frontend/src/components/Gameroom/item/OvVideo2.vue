<template>
  <video autoplay ref="video2" id="myVideo2" />
</template>

<script setup>
import { defineProps, onMounted, ref } from "vue";

const props = defineProps({ streamManager: Object });
const video2 = ref(null);
onMounted(() => {
  props.streamManager.addVideoElement(video2.value);
});
</script>

<style scoped>
#myVideo2 {
  border: 1px solid yellowgreen;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: -1;
  visibility: hidden;
}
</style>
