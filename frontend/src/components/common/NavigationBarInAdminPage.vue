<template>
  <div id="navbar">
    <div class="tab-info">
      <p :class="tab1">
        <span>
          <router-link to="/admin/game">게임 신고 관리</router-link>
        </span>
      </p>
      <p :class="tab2">
        <span>
          <router-link to="/admin/bestcut">베스트컷 신고 관리</router-link>
        </span>
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useStore } from "vuex";

const store = useStore();

const tab1 = computed(() => store.state.commonStore.tab.admin[0]);
const tab2 = computed(() => store.state.commonStore.tab.admin[1]);
</script>

<style scoped>
#navbar {
  background-color: #fdf8ec;
  height: 90vh;
}

a {
  text-decoration: none;
  line-height: 70px;
  margin: 40px;
  font-size: 24px;
  font-family: "NanumSquareRoundB";
  color: black;
}
.on {
  background-color: white;
  padding: 23px 0;
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  border-top: 1px solid #c9c9c9;
  border-left: 1px solid white;
  border-right: 1px solid #c9c9c9;
  border-bottom: 1px solid #c9c9c9;
}
.off {
  background-color: #fdf8ec;
  padding: 23px 0;
}
.tab-info {
  border-bottom: 1px solid #c9c9c9;
}
.tab-info p {
  text-align: right;
}
.tab-info > span {
  margin: 0 20px;
}
</style>
