<template>
  <div id="navbar" :class="view">
    <span class="left">
      <router-link to="/">똑딱</router-link>
    </span>
    <span class="mid">
      <router-link to="/gameList">게임 목록</router-link>
      <router-link to="/gameMake">게임 만들기</router-link>
      <router-link to="/bestcut">베스트 컷</router-link>
    </span>
    <span class="right">
      <a v-if="!accessToken" @click="setCurrentModalAsync(`login`)">로그인</a>
      <router-link v-if="accessToken" to="/member">마이 페이지</router-link>
      <a v-if="accessToken" @click="logout">로그아웃</a>
      <router-link v-if="isAdmin" to="/admin">관리자 페이지</router-link>
    </span>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { computed } from "vue";
import { useStore } from "vuex";

const store = useStore();
const api = apiInstance();

const view = computed(() => store.state.commonStore.view);
const accessToken = computed(() => store.state.memberStore.accessToken);
const isAdmin = computed(
  () => "ADMIN" == store.state.memberStore.memberInfo.role
);

const setCurrentModalAsync = (what) => {
  store.dispatch("commonStore/setCurrentModalAsync", {
    name: what,
    data: "",
  });
};

const logout = () => {
  api
    .get(`/api/members/logout`, {
      headers: {
        "access-token": accessToken.value, // 변수로 가지고있는 AccessToken
      },
    })
    .then((response) => {
      console.log(response);
      window.location.assign(`/`);
    })
    .catch((error) => {
      console.log(error);
    })
    .finally(() => {
      store.state.memberStore.$reset;
      // store.state.memberStore.memberInfo = {};
    });
};
</script>

<style scoped>
#navbar {
  height: 95px;
  position: relative;
}
.default {
  background-color: #fdf8ec;
}
.variant1 {
  background-color: #f87c7b;
}
.variant2 {
  background-color: #ffb800;
}
.variant3 {
  background-color: #77a4cc;
}
.default a,
.default span {
  color: black;
}
.variant1 a,
.variant2 a,
.variant3 a,
.variant1 span,
.variant2 span,
.variant3 span {
  color: white;
}
a {
  text-decoration: none;
  line-height: 95px;
  margin: 40px;
}

span:hover {
  cursor: pointer;
}

.left a {
  font-family: "Gugi-Regular";
  font-size: 48px;
}
.mid a {
  font-size: 24px;
  font-family: "NanumSquareRoundEB";
}
.right a {
  font-size: 24px;
  font-family: "NanumSquareRoundEB";
}
.right span {
  font-size: 24px;
  font-family: "NanumSquareRoundEB";
}

.left {
  float: left;
}

.mid {
  text-align: center;
  position: absolute;
  left: 50%;
  transform: translate(-50%, 0);
}

.right {
  float: right;
}
</style>
