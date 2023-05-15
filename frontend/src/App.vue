<template>
  <navigation-bar
    v-if="route.name !== `gameroom` && route.name !== 'mobileSingleGame'"
  />
  <router-view />
  <modal-frame v-if="currentModal.length !== 0" />
</template>

<script setup>
import NavigationBar from "@/components/common/NavigationBar";
import ModalFrame from "@/components/common/ModalFrame";
import { computed } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";

const route = useRoute();
const store = useStore();

const currentModal = computed(() => store.state.commonStore.currentModal);
</script>

<style>
@font-face {
  font-family: "Gugi-Regular";
  src: url("@/assets/fonts/Gugi-Regular.ttf") format("truetype");
}
@font-face {
  font-family: "NanumSquareRoundEB";
  src: url("@/assets/fonts/NanumSquareRoundEB.ttf") format("truetype");
}
@font-face {
  font-family: "NanumSquareRoundB";
  src: url("@/assets/fonts/NanumSquareRoundB.ttf") format("truetype");
}
@font-face {
  font-family: "NanumSquareRoundR";
  src: url("@/assets/fonts/NanumSquareRoundR.ttf") format("truetype");
}
@font-face {
  font-family: "SDSamliphopangcheTTFOutline";
  src: url("@/assets/fonts/SDSamliphopangcheTTFOutline.ttf") format("truetype");
}
#app {
  font-family: "NanumSquareRoundB";
}

* {
  margin: 0;
}

/* nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
} */
</style>
