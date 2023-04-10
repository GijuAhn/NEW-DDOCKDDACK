<template>
  <div id="nav" v-if="totalPageCount > 0">
    <ul class="pagination__inner">
      <li class="pagination-arrow">
        <button :disabled="previousButtonDisabled" @click="previous">
          <img
            v-if="!previousButtonDisabled"
            src="@/assets/images/left-arrow.png"
            width="24"
            heigth="24"
          />
          <img
            v-if="previousButtonDisabled"
            src="@/assets/images/left-arrow-disabled.png"
            width="24"
            heigth="24"
          />
        </button>
      </li>
      <li class="pagination-number" v-for="number in pageList" :key="number">
        <button :class="{ active: value === number }" @click="change(number)">
          {{ number }}
        </button>
      </li>
      <li class="pagination-arrow">
        <button :disabled="nextButtonDisabled" @click="next">
          <img
            v-if="!nextButtonDisabled"
            src="@/assets/images/right-arrow.png"
            width="24"
            heigth="24"
          />
          <img
            v-if="nextButtonDisabled"
            src="@/assets/images/right-arrow-disabled.png"
            width="24"
            heigth="24"
          />
        </button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, computed } from "vue";

const props = defineProps(["totalPageCount", "value"]);
const emit = defineEmits(["change"]);
const pageDisplayCount = 5;

// 현재 페이지의 그룹 번호 (현재 페이지 / 보여줄 페이지의 수)
const currentPageGroup = computed(() =>
  Math.ceil(props.value / pageDisplayCount)
);

// 마지막 페이지 번호
const lastPageNumber = computed(() => {
  const lastNumber = currentPageGroup.value * pageDisplayCount;
  if (lastNumber > props.totalPageCount) return props.totalPageCount;
  return lastNumber;
});

// 첫번째 페이지 번호
const firstPageNumber = computed(() => {
  // 끝 번호가 26,27 이렇게 끝날 경우 페이지를 [26,27] 이렇게 보여줘야 하기에 존재하는 로직
  if (lastPageNumber.value == props.totalPageCount) {
    const multipleOfPageDisplayCount =
      lastPageNumber.value % pageDisplayCount === 0;
    return multipleOfPageDisplayCount
      ? lastPageNumber.value - pageDisplayCount + 1
      : lastPageNumber.value - (lastPageNumber.value % pageDisplayCount) + 1;
  }
  return lastPageNumber.value - (pageDisplayCount - 1);
});

// 페이지 리스트 (pageDisplayCount가 5일 경우 [1~5], [6~10]...)
const pageList = computed(() => {
  const list = [];
  for (let i = firstPageNumber.value; i <= lastPageNumber.value; i++) {
    list.push(i);
  }
  return list;
});

// 다음 버튼 비활성화 조건
const nextButtonDisabled = computed(
  () => lastPageNumber.value >= props.totalPageCount
);

// 이전 버튼 비활성화 조건
const previousButtonDisabled = computed(() => firstPageNumber.value <= 1);

// 사용자가 번호를 변경하는 경우 상위 컴포넌트로 값 전달
const change = (clickNumber) => {
  if (clickNumber === props.value) return false;
  emit("change", clickNumber);
};
// 이전 버튼 클릭 시 이전 페이지의 첫번째 값으로 설정
const previous = () => {
  emit("change", firstPageNumber.value - pageDisplayCount);
};
// 다음 버튼 클릭 시 이후 페이지의 첫번째 값으로 설정
const next = () => {
  emit("change", lastPageNumber.value + 1);
};
</script>

<style scoped>
#nav {
  margin-top: 70px;
  /* background-color: aquamarine; */
  text-align: center;
}

ul {
  /* border: 1px solid red; */
  display: inline-block;
  list-style: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
  vertical-align: middle;
}
.pagination-arrow button {
  width: 40px;
  height: 40px;
  background-color: white;
  border: none;
}
.pagination-arrow button:hover {
  cursor: pointer;
}
.pagination-number button {
  width: 40px;
  height: 40px;
  background-color: white;
  border: none;
  font-family: NanumSquareRoundB;
  font-size: 20px;
  border-radius: 50%;
}
.pagination-number button:hover {
  cursor: pointer;
}
.active {
  background-color: #c9c9c9 !important;
}
</style>
