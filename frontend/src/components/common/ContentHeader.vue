<template>
  <div id="header" :class="view">
    <div id="header-name">
      <span v-if="view === `variant1`">게임 목록</span>
      <span v-if="view === `variant2`">게임 만들기</span>
      <span v-if="view === `variant3`">베스트 컷</span>
    </div>
  </div>
  <div></div>
</template>

<script setup>
import { computed } from "vue";
import { useStore } from "vuex";

const store = useStore();

const view = computed(() => store.state.commonStore.view);
</script>

<style scoped>
#header-name {
  margin-bottom: -100px;
}

#header {
  height: 790px;
  position: relative;
  z-index: -1;
}
.variant1 {
  background-color: #f87c7b;
}
.variant2 {
  background-color: #ffb800;
}
.variant3 {
  background-color: #77a4cc;
}
span {
  color: white;
  font-family: "SDSamliphopangcheTTFOutline";
  font-size: 80px;
  position: absolute;
  top: 280px;
  left: 50%;
  transform: translate(-50%, 0);
}
</style>
