<template>
  <div>
    <content-header />
    <game-list></game-list>
  </div>
</template>

<script setup>
import GameList from "@/components/GameList/GameList.vue";
import ContentHeader from "@/components/common/ContentHeader";

import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant1");
</script>

<style scoped></style>
