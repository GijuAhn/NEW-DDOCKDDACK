<template>
  <div>
    <content-header />
    <bestcut-list />
    <modal-frame v-if="currentModal.length !== 0" />
  </div>
</template>

<script setup>
import ContentHeader from "@/components/common/ContentHeader";
import BestcutList from "@/components/BestcutList/BestcutList";
import ModalFrame from "@/components/common/ModalFrame";

import { useStore } from "vuex";
import { computed } from "vue";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant3");
const currentModal = computed(() => store.state.commonStore.currentModal);
</script>

<style></style>
