<template>
  <div id="view">
    <h1>게임 신고 관리</h1>
    <table>
      <colgroup span="6" class="columns"></colgroup>
      <thead>
        <tr>
          <th>게임 제목</th>
          <th>신고 사유</th>
          <th>신고한 유저</th>
          <th>신고당한 유저</th>
          <th>상세 보기</th>
          <th>처리</th>
        </tr>
      </thead>
      <tbody v-if="reportedGames">
        <reported-game
          v-for="reportedGame in reportedGames"
          :key="reportedGame"
          :reportedGame="reportedGame"
          @deleteGame="(target) => deleteGame(target)"
          @deleteReport="(target) => deleteReport(target)"
        ></reported-game>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { ref, computed } from "vue";
import { apiInstance } from "@/api/index";
import ReportedGame from "@/components/Admin/item/ReportedGame";

const api = apiInstance();
const store = useStore();
const admin_api_url = `/api/admin`;
const accessToken = computed(() => store.state.memberStore.accessToken);

let reportedGames = ref();

const callApi = () => {
  api
    .get(admin_api_url + `/reported/games`, {
      headers: { "access-token": accessToken.value },
      params: {},
    })
    .then((response) => {
      reportedGames.value = response.data;
      console.log(reportedGames);
    })
    .catch((error) => {
      console.log(error);
    });
};

callApi();

const deleteGame = function (target) {
  reportedGames.value = reportedGames.value.filter(
    (item) => item.gameId !== target.value
  );
  console.log(reportedGames);
};

const deleteReport = function (target) {
  reportedGames.value = reportedGames.value.filter(
    (item) => item.reportId !== target.value
  );
};

store.dispatch("commonStore/setAdminTabAsync", 0);
</script>

<style scoped>
#view {
  background-color: white;
  padding: 80px;
}

table {
  margin-top: 80px;
  text-align: center;
}

thead {
  font-size: 30px;
}

tfoot {
  font-size: 20px;
}

th {
  width: 11vw;
  height: 10vh;
}

td {
  height: 50px;
}

td,
tr,
th {
  border-bottom: 1px solid #737373;
}
</style>
