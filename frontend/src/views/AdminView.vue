<template>
  <div class="flex-container">
    <navigation-bar-in-admin-page />
    <router-view></router-view>
  </div>
</template>

<script setup>
import NavigationBarInAdminPage from "@/components/common/NavigationBarInAdminPage";
import { useStore } from "vuex";

const store = useStore();

store.dispatch("commonStore/setColorAsync", "default");
</script>

<style scoped>
.flex-container {
  display: flex;
}
</style>
