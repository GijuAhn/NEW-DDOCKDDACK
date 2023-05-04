<template>
  <div>
    <single-games></single-games>
  </div>
</template>

<script setup>
import SingleGames from "@/components/GameList/MobileSingleGameList.vue";

import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant1");
</script>

<style scoped></style>
