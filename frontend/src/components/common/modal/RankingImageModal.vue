<template>
  <div id="rankingImage">
    <img :src="`${IMAGE_PATH}/${currentModal.data}`" width="500" height="500" />
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import process from "process";
import { computed } from "vue";

const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

const store = useStore();

const currentModal = computed(() => store.state.commonStore.currentModal);
</script>

<style scoped>
#rankingImage {
  background-color: white;
  padding: 5px;
}
img {
  margin: 2px;
  object-fit: cover;
  transform: scaleX(-1);
}
</style>
