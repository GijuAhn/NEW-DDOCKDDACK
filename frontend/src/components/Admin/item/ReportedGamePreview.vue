<template>
  <div v-if="game">
    <div id="previewImages">
      <img
        v-for="image in game.images"
        :key="image"
        :src="`${IMAGE_PATH}/${image.gameImage}`"
        width="120"
        height="120"
      />
    </div>
  </div>
</template>

<script setup>
import process from "process";
import { defineProps, ref } from "vue";
import { apiInstance } from "@/api/index";

const props = defineProps({ gameId: String });

const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

const api = apiInstance();

const game = ref();

api
  .get(`/api/games/${props.gameId}`)
  .then((response) => {
    console.log(response.data);
    game.value = response.data;
  })
  .catch((error) => {
    console.log(error);
  });
</script>

<style scoped>
#previewImages {
  background-color: white;
  display: grid;
  grid-template-rows: repeat(2, 1fr);
  grid-template-columns: repeat(10, 1fr);
  padding: 10px;
}
img {
  margin: 2px;
  object-fit: cover;
}
</style>
