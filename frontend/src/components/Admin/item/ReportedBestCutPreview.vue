<template>
  <div v-if="bestcut">
    <img
      :src="`${IMAGE_PATH}/${bestcut.gameImgUrl}`"
      alt="원본사진"
      class="image"
    />
    <img
      :src="`${IMAGE_PATH}/${bestcut.bestcutImgUrl}`"
      alt="베스트컷"
      class="image"
    />
  </div>
</template>

<script setup>
import process from "process";
import { defineProps, ref } from "vue";
import { apiInstance } from "@/api/index";

const props = defineProps({ bestcutId: String });

const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

const api = apiInstance();

const bestcut = ref();

api
  .get(`/api/bestcuts/${props.bestcutId}`)
  .then((response) => {
    console.log(response.data);
    bestcut.value = response.data;
  })
  .catch((error) => {
    console.log(error);
  });
</script>

<style scoped>
.image {
  width: 400px;
  height: 360px;
  object-fit: cover;
  display: inline-block;
  margin-top: 14px;
  margin-left: 68px;
}
</style>
