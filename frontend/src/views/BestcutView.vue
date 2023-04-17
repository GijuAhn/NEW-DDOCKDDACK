<template>
  <div>
    <content-header />
    <bestcut-list />
  </div>
</template>

<script setup>
import ContentHeader from "@/components/common/ContentHeader";
import BestcutList from "@/components/BestcutList/BestcutList";

import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant3");
</script>

<style></style>
