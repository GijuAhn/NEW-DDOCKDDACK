<template>
  <div id="ovVideoContainer" v-if="props.streamManager">
    <ov-video
      id="ovVideo"
      :class="{ blinking: captureMode }"
      :stream-manager="props.streamManager"
    />

    <div id="nickname" v-if="props.isSub">
      <span>{{ clientData }}</span>
      <span>
        <svg-icon
          v-if="isSubVideoEnable"
          @click="subVideoChange"
          type="mdi"
          :path="path[0]"
        ></svg-icon>
        <svg-icon
          v-if="!isSubVideoEnable"
          @click="subVideoChange"
          type="mdi"
          :path="path[1]"
        ></svg-icon>
      </span>
      <span>
        <svg-icon
          v-if="isSubAudioEnable"
          @click="subAudioChange"
          type="mdi"
          :path="path[2]"
        ></svg-icon>
        <svg-icon
          v-if="!isSubAudioEnable"
          @click="subAudioChange"
          type="mdi"
          :path="path[3]"
        ></svg-icon>
      </span>
    </div>
  </div>
</template>

<script setup>
import OvVideo from "./OvVideo";

import { computed, defineProps, ref } from "vue";

import SvgIcon from "@jamescoyle/vue-icon";
import {
  mdiMonitor,
  mdiMonitorOff,
  mdiVolumeHigh,
  mdiVolumeOff,
} from "@mdi/js";

const isSubVideoEnable = ref(true);
const isSubAudioEnable = ref(true);
const props = defineProps({
  streamManager: Object,
  captureMode: Boolean,
  isSub: Boolean,
});
const path = ref([mdiMonitor, mdiMonitorOff, mdiVolumeHigh, mdiVolumeOff]);

const clientData = computed(() => {
  const { clientData } = getConnectionData();
  return clientData;
});

const getConnectionData = () => {
  const { connection } = props.streamManager.stream;
  return JSON.parse(connection.data);
};

const subVideoChange = () => {
  isSubVideoEnable.value = !isSubVideoEnable.value;
  props.streamManager.subscribeToVideo(isSubVideoEnable.value);
};

const subAudioChange = () => {
  isSubAudioEnable.value = !isSubAudioEnable.value;
  props.streamManager.subscribeToAudio(isSubAudioEnable.value);
};
</script>

<style scoped>
#ovVideoContainer {
  position: relative;
}
#ovVideo:hover ~ #nickname {
  display: flex;
  align-items: center;
}
#nickname {
  position: absolute;
  bottom: 14px;
  left: 20px;
  padding: 2px;
  display: none;
  background-color: black;
  /* border: 1px solid red; */
  vertical-align: middle;
}
#nickname:hover {
  display: flex;
  align-items: center;
}
#nickname > span:nth-child(1) {
  font-size: 14px;
  font-family: NanumSquareRoundR;
  /* border: 1px solid yellow; */
  margin-right: 10px;
}
#nickname > span:nth-child(2) {
  width: 16px;
  height: 16px;
  /* border: 1px solid yellow; */
  display: inline-block;
  margin-right: 5px;
}
#nickname > span:nth-child(3) {
  width: 17px;
  height: 17px;
  /* border: 1px solid yellow; */
  display: inline-block;
}
svg {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
svg:hover {
  cursor: pointer;
}

.blinking {
  animation: blink 0.5s ease-in-out infinite alternate;
}
@keyframes blink {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>
