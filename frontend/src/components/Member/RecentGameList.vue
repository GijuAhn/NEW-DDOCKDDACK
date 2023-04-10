<template>
  <div id="view">
    <table>
      <colgroup span="3" class="columns"></colgroup>
      <thead>
        <tr>
          <th>게임 제목</th>
          <th>내 순위</th>
          <th>날짜</th>
        </tr>
      </thead>
      <tr
        v-for="recentGame in recentGames"
        :key="recentGame"
        :recentGame="recentGame"
      >
        <td>{{ recentGame.gameTitle }}</td>
        <td>{{ recentGame.ranking }}</td>
        <td>{{ recentGame.playedTime }}</td>
      </tr>
    </table>
    <div
      id="noItem"
      v-show="(!recentGames || !recentGames.length) && !isLoading"
    >
      최근 플레이한 게임이 없습니다!
    </div>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { useStore } from "vuex";
import { ref, computed } from "vue";

const store = useStore();

const api = apiInstance();
const accessToken = computed(() => store.state.memberStore.accessToken).value;

const recentGames = ref();
const callApi = () => {
  api
    .get(`/api/members/records`, {
      headers: {
        "access-token": accessToken, // 변수로 가지고있는 AccessToken
      },
    })
    .then((response) => {
      // console.log("access-games: ", response.data);
      recentGames.value = response.data;
    })
    .catch((error) => {
      console.log(error);
      // if (error.response.status == 401) {
      //   getAccessTokenByRefreshToken(); // refresh 토큰으로 다시 accessToken불러오기. 성공하면 -> watch로 accessToken 변경감지해서 다시 실행???
      // }
    });
};

callApi();

store.dispatch("commonStore/setMemberTabAsync", 0);
</script>

<style scoped>
#view {
  width: 1200px;
  position: relative;
  left: 50%;
  transform: translate(-50%, 0);
  background-color: white;
}

#list {
  display: grid;
  gap: 35px 0;
  grid-template-columns: repeat(3, 1fr);
  width: 500px;
  margin: 2%;
}

table {
  width: 100%;
  margin-top: 10px;
  text-align: center;
  margin-bottom: 20%;
}

thead {
  font-size: 30px;
}

th {
  height: 90px;
}

td {
  height: 60px;
}

td,
tr,
th {
  border-bottom: 1px solid #737373;
}

#noItem {
  font-size: 20px;
  margin-left: 40%;
}
</style>
