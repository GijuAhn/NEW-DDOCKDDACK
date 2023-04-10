<template>
  <div>
    <div id="reportModal">
      <div id="reportTitle">
        <div><span>베스트컷 신고</span></div>
      </div>
      <div id="reportReason">
        <div @click="reportBestcut(`SPAM`)"><span>스팸</span></div>
        <div @click="reportBestcut(`SEXUAL`)"><span>선정적</span></div>
        <div @click="reportBestcut(`VIOLENT`)"><span>폭력적</span></div>
        <div @click="reportBestcut(`DISGUSTING`)"><span>혐오스러움</span></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { computed } from "vue";
import { apiInstance } from "@/api/index";

const api = apiInstance();
const store = useStore();
const currentModal = computed(() => store.state.commonStore.currentModal);
const accessToken = computed(() => store.state.memberStore.accessToken).value;

const reportBestcut = (reportType) => {
  if (!accessToken) {
    alert("로그인 후 이용해주세요.");
    setCurrentModalAsync("catch");
  }
  api
    .post(
      `/api/bestcuts/report/${currentModal.value.data.bestcutId}`,
      {
        reportType: reportType,
      },
      { headers: { "access-token": accessToken } }
    )
    .then(() => {
      setCurrentModalAsync("then");
    })
    .catch((error) => {
      if (error.response.status == 400) {
        alert("이미 신고한 베스트컷입니다!");
      }
      setCurrentModalAsync("catch");
    });
};
const setCurrentModalAsync = (value) => {
  if (value === "then") {
    store.dispatch("commonStore/setCurrentModalAsync", {
      name: "reportComplete",
      data: "",
    });
  } else {
    store.dispatch("commonStore/setCurrentModalAsync", "");
  }
};
</script>

<style scoped>
#reportModal {
  background-color: white;
  width: 235px;
  border-radius: 10px;
  padding: 5px 0;
}
#reportModal span {
  display: block;
}

#reportTitle {
  text-align: center;
  border-bottom: 1px solid #a0a0a0;
}
#reportTitle span {
  font-size: 19px;
  padding: 10px;
}
#reportReason span {
  font-size: 16px;
  padding: 12px;
  margin: 1px;
  border-bottom: 1px solid #eaeaea;
}
#reportReason:hover {
  cursor: pointer;
}
#reportReason div:last-child span {
  border-bottom: none;
}
</style>
