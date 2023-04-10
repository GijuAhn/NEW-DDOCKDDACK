<template>
  <tr>
    <td>{{ props.reportedBestCut.bestcutTitle }}</td>
    <td>{{ props.reportedBestCut.reason }}</td>
    <td>{{ props.reportedBestCut.reportMemberNickname }}</td>
    <td>{{ props.reportedBestCut.reportedMemberNickname }}</td>
    <td><input v-model="visible" type="checkbox" /></td>
    <td>
      <select v-model="banLevel" class="action">
        <option value="noPenalty">noPenalty</option>
        <option value="oneWeek" selected>oneWeek</option>
        <option value="oneMonth">oneMonth</option>
        <option value="sixMonth">sixMonth</option>
        <option value="oneYear">oneYear</option>
        <option value="endless">endless</option>
      </select>
      <button @click="punishmentApi()" class="action" id="red">처리</button>
      <button @click="reportcancelApi()" class="action">취소</button>
    </td>
  </tr>
  <tr v-if="visible">
    <td colspan="6">
      <reported-best-cut-preview
        :bestcutId="bestcutId"
      ></reported-best-cut-preview>
    </td>
  </tr>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { useStore } from "vuex";
import { defineProps, ref, computed, defineEmits } from "vue";
import ReportedBestCutPreview from "@/components/Admin/item/ReportedBestCutPreview";

const api = apiInstance();
const props = defineProps({ reportedBestCut: Object });
const visible = ref();
const store = useStore();
const admin_api_url = `/api/admin`;
const accessToken = computed(() => store.state.memberStore.accessToken);
const banLevel = "noPenalty";
const emit = defineEmits(["deleteBestcut", "deleteReport"]);
const bestcutId = computed(() => {
  return props.reportedBestCut.bestcutId;
});

const punishmentApi = () => {
  api
    .delete(
      admin_api_url +
        `/remove/bestcut/${props.reportedBestCut.reportId}/${props.reportedBestCut.bestcutId}`,
      {
        headers: {
          "access-token": accessToken.value,
          banMemberId: props.reportedBestCut.reportedMemberId,
          banLevel: banLevel,
        },
      }
    )
    .then((response) => {
      console.log(response);
      emit("deleteBestcut", { value: props.reportedBestCut.bestcutId });
    })
    .catch((error) => {
      error;
      console.log(error);
    });
};

const reportcancelApi = () => {
  api
    .delete(
      admin_api_url + `/remove/bestcut/${props.reportedBestCut.reportId}`,
      {
        headers: {
          "access-token": accessToken.value,
        },
      }
    )
    .then((response) => {
      console.log(response);
      emit("deleteReport", { value: props.reportedBestCut.reportId });
    })
    .catch((error) => {
      error;
      console.log(error);
    });
};
</script>

<style scoped>
td {
  height: 50px;
  border-bottom: 1px solid #737373;
}

input {
  margin-top: 3px;
  width: 25px;
  height: 25px;
}

.action {
  margin: 2px;
  height: 40px;
  background-color: white;
  border-radius: 5px;
  border: 2px solid black;
  font-size: 17px;
  font-family: "NanumSquareRoundB";
}

#red {
  background-color: #d33a3a;
  color: white;
}
</style>
