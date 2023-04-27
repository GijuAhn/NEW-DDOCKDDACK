<template>
  <div>
    <content-header />
    <single-games></single-games>
  </div>
</template>

<script setup>
import SingleGames from "@/components/GameList/SingleGameList.vue";
import ContentHeader from "@/components/common/ContentHeader";

import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant1");
</script>

<style scoped></style>
