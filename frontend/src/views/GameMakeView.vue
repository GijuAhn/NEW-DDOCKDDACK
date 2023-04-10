<template>
  <content-header />
  <router-view></router-view>
</template>

<script setup>
import ContentHeader from "@/components/common/ContentHeader";

import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "variant2");
</script>

<style></style>
