<template>
  <div>
    <content-header />
    <multi-games></multi-games>
  </div>
</template>

<script setup>
import MultiGames from "@/components/GameList/MultiGameList.vue";
import ContentHeader from "@/components/common/ContentHeader";

import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant1");
</script>

<style scoped></style>
