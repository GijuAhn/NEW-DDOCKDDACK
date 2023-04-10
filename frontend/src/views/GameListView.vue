<template>
  <div>
    <content-header />
    <game-list></game-list>
    <modal-frame v-if="currentModal.length !== 0" />
  </div>
</template>

<script setup>
import GameList from "@/components/GameList/GameList.vue";
import ContentHeader from "@/components/common/ContentHeader";
import ModalFrame from "@/components/common/ModalFrame";

import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant1");

import { computed } from "vue";

const currentModal = computed(() => store.state.commonStore.currentModal);
</script>

<style scoped></style>
