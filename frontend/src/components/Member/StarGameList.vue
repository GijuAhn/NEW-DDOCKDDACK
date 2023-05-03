<template>
  <div id="view">
    <div id="list" v-if="!isLoading">
      <normal-game
        v-for="(game, index) in starredGames"
        :key="index"
        :game="game"
        :index="index"
        @updateProps="(value) => updateMyGames(value)"
      ></normal-game>
    </div>
    <loading-spinner id="imgLoading" v-if="isLoading">
      <!-- 이미지 로딩 중 -->
    </loading-spinner>
    <!-- <div id="noItem" v-show="!starredGames && !isLoading">
      즐겨찾기한 게임이 없습니다!
    </div> -->
  </div>
</template>

<script setup>
import NormalGame from "@/components/GameList/item/NormalGame";
import { apiInstance } from "@/api/index";
import { useStore } from "vuex";
import { ref, computed } from "vue";
import LoadingSpinner from "./item/LoadingSpinner.vue";

const store = useStore();

const api = apiInstance();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const isLoading = ref(true);

const starredGames = ref();
const callApi = async () => {
  await api
    .get(`/api/members/starred`, {
      headers: {
        "access-token": accessToken, // 변수로 가지고있는 AccessToken
      },
    })
    .then((response) => {
      if (response.data.length > 0) {
        starredGames.value = response.data;
      }
    })
    .catch((error) => {
      console.log(error);
    })
    .finally(() => {
      isLoading.value = false;
    });
};

const updateMyGames = (value) => {
  starredGames.value.splice(value, 1);
};
callApi();

store.dispatch("commonStore/setMemberTabAsync", 1);
</script>

<style scoped>
#view {
  width: 100%;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}
#list {
  display: grid;
  gap: 35px 0;
  grid-template-columns: repeat(3, 1fr);
  width: 1090px;
  margin: 2%;
  text-align: left;
}
#noItem {
  font-size: 20px;
}
</style>
