<template>
  <div v-if="props.streamManager">
    <ov-video2 class="child1" :stream-manager="props.streamManager" />

    <div v-if="false">
      <p>{{ clientData }}</p>
    </div>
  </div>
</template>

<script setup>
import OvVideo2 from "./OvVideo2";

import { computed, defineProps } from "vue";

const props = defineProps({
  streamManager: Object,
});

const clientData = computed(() => {
  const { clientData } = getConnectionData();
  return clientData;
});

const getConnectionData = () => {
  const { connection } = props.streamManager.stream;
  return JSON.parse(connection.data);
};
</script>

<style scoped></style>
