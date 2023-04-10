<template>
  <div id="modal">
    <div id="modalBackground" @click="setCurrentModalAsync"></div>
    <preview-modal
      class="modalContent"
      v-if="currentModal.name === `preview`"
    />
    <report-reason-modal
      class="modalContent"
      v-if="currentModal.name === `reportReason`"
    />
    <report-complete-modal
      class="modalContent"
      v-if="currentModal.name === `reportComplete`"
    />
    <bestcut-detail-modal
      class="modalContent"
      v-if="currentModal.name === `bestcutDetail`"
    />
    <bestcut-report-modal
      class="modalContent"
      v-if="currentModal.name === `bestcutReport`"
    />
    <login-modal class="modalContent" v-if="currentModal.name === `login`" />
    <bestcut-upload-modal
      class="modalContent"
      v-if="currentModal.name === `bestcutUpload`"
    />
    <intermediate-result-modal
      class="modalContent"
      v-if="currentModal.name === `intermediateResult`"
    />
    <final-result-modal
      class="modalContent"
      v-if="currentModal.name === `finalResult`"
    />
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import PreviewModal from "@/components/common/modal/PreviewModal";
import ReportReasonModal from "@/components/common/modal/ReportReasonModal";
import ReportCompleteModal from "@/components/common/modal/ReportCompleteModal";
import BestcutDetailModal from "@/components/common/modal/BestcutDetailModal";
import BestcutReportModal from "@/components/common/modal/BestcutReportModal";
import LoginModal from "@/components/common/modal/LoginModal";
import BestcutUploadModal from "@/components/common/modal/BestcutUploadModal";
import IntermediateResultModal from "@/components/common/modal/IntermediateResultModal";
import FinalResultModal from "@/components/common/modal/FinalResultModal";

import { computed } from "vue";

const store = useStore();

const currentModal = computed(() => store.state.commonStore.currentModal);

const setCurrentModalAsync = () => {
  store.dispatch("commonStore/setCurrentModalAsync", "");
};
</script>

<style scoped>
#modal {
  position: fixed;
  min-width: calc(1920px - 100px);
  width: 100%;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
#modalBackground {
  width: 100%;
  height: 100%;
  background-color: black;
  opacity: 0.5;
}
.modalContent {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
