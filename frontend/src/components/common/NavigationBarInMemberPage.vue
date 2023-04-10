<template>
  <div id="navbar">
    <div id="profile">
      <my-profile />
    </div>
    <div class="tab-info">
      <span :class="tab4"
        ><router-link to="/member/myBestcut">나의 베스트 컷</router-link></span
      >
      <span :class="tab2"
        ><router-link to="/member/starGame">즐겨찾기</router-link></span
      >
      <span :class="tab3"
        ><router-link to="/member/myGame">내가 만든 게임</router-link></span
      >
      <span :class="tab1">
        <router-link to="/member/recentGame">최근 플레이</router-link></span
      >
    </div>
  </div>
</template>

<script setup>
import MyProfile from "@/components/common/MyProfile";

import { computed } from "vue";
import { useStore } from "vuex";

const store = useStore();

const tab1 = computed(() => store.state.commonStore.tab.member[0]);
const tab2 = computed(() => store.state.commonStore.tab.member[1]);
const tab3 = computed(() => store.state.commonStore.tab.member[2]);
const tab4 = computed(() => store.state.commonStore.tab.member[3]);
</script>

<style scoped>
#navbar {
  background-color: #fdf8ec;

  border-bottom: 1px solid #c9c9c9;
}
a {
  text-decoration: none;
  line-height: 65px;
  margin: 40px;
  font-size: 24px;
  font-family: "NanumSquareRoundB";
  color: black;
}
.on {
  background-color: white;
  padding: 23px 0;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  border-top: 1px solid #c9c9c9;
  border-left: 1px solid #c9c9c9;
  border-right: 1px solid #c9c9c9;
  border-bottom: 5px solid white;
}
.off {
  background-color: #fdf8ec;
  padding: 23px 0;
}
.tab-info {
  height: 68px;
  margin-left: 15%;
  /* border-bottom: 1px solid #c9c9c9; */
}
.tab-info span {
  margin: 0 20px;
}
#profile {
  margin-left: 20%;
}
</style>
