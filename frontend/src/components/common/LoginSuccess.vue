<template>
  <div></div>
</template>

<script setup>
import router from "@/router";
import { onBeforeMount } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";

const store = useStore();
const route = useRoute();

onBeforeMount(async () => {
  await router.isReady();

  const accessToken = route.query.accessToken;

  if (accessToken) {
    store.dispatch("memberStore/setTokensAsync", accessToken);
    store.dispatch("memberStore/getMemberInfo");
  }
  router.push("/");
});
</script>

<style></style>
