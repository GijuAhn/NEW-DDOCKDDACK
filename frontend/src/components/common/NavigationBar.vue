<template>
  <div id="navbar" :class="view">
    <span class="left">
      <router-link class="b" to="/">똑딱</router-link>
    </span>
    <span class="mid">
      <router-link class="b" to="/single-games">혼자 하기</router-link>
      <router-link class="b" to="/multi-games">같이 하기</router-link>
      <router-link v-if="accessToken" class="b" to="/gameMake"
        >게임 만들기</router-link
      >
      <router-link class="b" to="/bestcut">베스트 컷</router-link>
    </span>
    <span class="right">
      <a v-if="!accessToken" class="b" @click="setCurrentModalAsync(`login`)"
        >로그인</a
      >
      <router-link class="b" v-if="accessToken" to="/member"
        >마이 페이지</router-link
      >
      <a class="b" v-if="accessToken" @click="logout">로그아웃</a>
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
.b {
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
.mid span {
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
.c span {
  font-size: 18px;
  font-family: "NanumSquareRoundEB";
  color: black;
}

.left {
  float: left;
}

.mid {
  text-align: center;
  position: absolute;
  left: 50%;
  transform: translate(-50%, 0);
  z-index: 1;
}

.right {
  float: right;
}

/* #content:hover {
  box-shadow: 0 0 20px #8b8b8b;
  transition: 0.3s;
} */
</style>
