<template>
  <div id="view">
    <h1>베스트 컷 신고 관리</h1>
    <table>
      <colgroup span="6" class="columns"></colgroup>
      <thead>
        <tr>
          <th>베스트 컷 제목</th>
          <th>신고 사유</th>
          <th>신고한 유저</th>
          <th>신고당한 유저</th>
          <th>상세 보기</th>
          <th>처리</th>
        </tr>
      </thead>
      <tbody v-if="reportedBestCuts">
        <reported-best-cut
          v-for="reportedBestCut in reportedBestCuts"
          :key="reportedBestCut"
          :reportedBestCut="reportedBestCut"
          @deleteBestcut="(target) => deleteBestcut(target)"
          @deleteReport="(target) => deleteReport(target)"
        ></reported-best-cut>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { ref, computed } from "vue";
import { apiInstance } from "@/api/index";
import ReportedBestCut from "@/components/Admin/item/ReportedBestCut";

const api = apiInstance();
const store = useStore();
const admin_api_url = `/api/admin`;
const accessToken = computed(() => store.state.memberStore.accessToken);

let reportedBestCuts = ref();

const callApi = () => {
  api
    .get(admin_api_url + `/reported/bestcuts`, {
      headers: { "access-token": accessToken.value },
      params: {},
    })
    .then((response) => {
      console.log(response);
      reportedBestCuts.value = response.data;
      console.log(reportedBestCuts);
    })
    .catch((error) => {
      error;
      console.log(error);
    });
};

callApi();

const deleteReport = function (target) {
  reportedBestCuts.value = reportedBestCuts.value.filter(
    (item) => item.reportId !== target.value
  );
};

const deleteBestcut = function (target) {
  reportedBestCuts.value = reportedBestCuts.value.filter(
    (item) => item.bestcutId !== target.value
  );
};

store.dispatch("commonStore/setAdminTabAsync", 1);
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
