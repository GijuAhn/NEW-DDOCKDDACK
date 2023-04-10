<template>
  <button v-if="startPage > 1" @click="changePage(startPage--)">prev</button>
  <ul>
    <li v-for="n in 5" :key="n">
      <button
        v-if="startPage + n <= totalPages"
        @click="changePage(startPage + n)"
      >
        {{ startPage + n }}
      </button>
    </li>
  </ul>
  <button v-if="endPage < totalPages" @click="changePage(endPage++)">
    next
  </button>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({ totalPages: Number, page: Number });
let startPage = ((props.page - 1) / 5) * 5;
let endPage = startPage + 4;

const emit = defineEmits(["prev", "changePage", "next"]);

const changePage = (number) => {
  emit("changePage", number);
};
</script>

<style></style>
