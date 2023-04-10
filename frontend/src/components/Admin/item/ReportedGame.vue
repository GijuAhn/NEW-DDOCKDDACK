<template>
  <tr>
    <td>{{ props.reportedGame.gameTitle }}</td>
    <td>{{ props.reportedGame.reason }}</td>
    <td>{{ props.reportedGame.reportMemberNickname }}</td>
    <td>{{ props.reportedGame.reportedMemberNickname }}</td>
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
      <div class="preview">
        <reported-game-preview :gameId="gameId"></reported-game-preview>
      </div>
    </td>
  </tr>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { useStore } from "vuex";
import { defineProps, ref, computed, defineEmits } from "vue";
import ReportedGamePreview from "@/components/Admin/item/ReportedGamePreview";

const api = apiInstance();
const props = defineProps({ reportedGame: Object });
const visible = ref();
const store = useStore();
const admin_api_url = `/api/admin`;
const accessToken = computed(() => store.state.memberStore.accessToken);
const banLevel = "noPenalty";
const emit = defineEmits(["deleteGame", "deleteReport"]);
const gameId = computed(() => {
  return props.reportedGame.gameId;
});

const punishmentApi = () => {
  api
    .delete(
      admin_api_url +
        `/remove/game/${props.reportedGame.reportId}/${props.reportedGame.gameId}`,
      {
        headers: {
          "access-token": accessToken.value,
          banMemberId: props.reportedGame.reportedMemberId,
          banLevel: banLevel,
        },
      }
    )
    .then((response) => {
      console.log(response);
      emit("deleteGame", { value: props.reportedGame.gameId });
    })
    .catch((error) => {
      error;
      console.log(error);
    });
};

const reportcancelApi = () => {
  api
    .delete(admin_api_url + `/remove/game/${props.reportedGame.reportId}`, {
      headers: {
        "access-token": accessToken.value,
      },
    })
    .then((response) => {
      console.log(response);
      emit("deleteReport", { value: props.reportedGame.reportId });
    })
    .catch((error) => {
      error;
      console.log(error);
    });
};
</script>

<style scoped>
.preview {
  display: flex;
  justify-content: center; /* flex-start / center / flex-end */
}

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
