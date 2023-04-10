import { createRouter, createWebHistory } from "vue-router";
// import { useStore } from "vuex";
import { computed } from "vue";
import store from "@/store";
import MainView from "@/views/MainView.vue";

const authMember = async (to, from, next) => {
  let accessToken = computed(() => store.state.memberStore.accessToken).value;
  let memberInfo = computed(() => store.state.memberStore.memberInfo).value;

  if (accessToken === "") {
    await store.dispatch("memberStore/accesstokenReissue", true);
    accessToken = computed(() => store.state.memberStore.accessToken).value;
  }
  if (accessToken !== "" && memberInfo.email === "") {
    await store.dispatch("memberStore/getMemberInfo");
  }
  accessToken = computed(() => store.state.memberStore.accessToken).value;
  next();
};

const isLogin = async () => {
  let accessToken = computed(() => store.state.memberStore.accessToken).value;
  let memberInfo = computed(() => store.state.memberStore.memberInfo).value;

  if (accessToken === "") {
    await store.dispatch("memberStore/accesstokenReissue", false);
    accessToken = computed(() => store.state.memberStore.accessToken).value;
  }
  if (accessToken !== "" && memberInfo.email === "") {
    await store.dispatch("memberStore/getMemberInfo");
  }
  accessToken = computed(() => store.state.memberStore.accessToken).value;
  console.log("isLogin ", accessToken);
};

const routes = [
  {
    path: "/",
    name: "main",
    component: MainView,
    beforeEnter: isLogin,
  },
  {
    path: "/gameList",
    name: "gameList",
    beforeEnter: isLogin,
    component: () => import("@/views/GameListView.vue"),
  },
  {
    path: "/gameMake",
    name: "gameMake",
    beforeEnter: isLogin,
    component: () => import("@/views/GameMakeView.vue"),
    redirect: "/gameMake/createGame",
    children: [
      {
        path: "createGame",
        name: "createGame",
        // beforeEnter: authMember, --> 로그인된 회원만 됨
        component: () => import("@/components/GameMake/CreateGame.vue"),
      },
    ],
  },
  {
    path: "/bestcut",
    name: "bestcutList", //bestcut 중복 체크
    beforeEnter: isLogin,
    component: () => import("@/views/BestcutView.vue"),
  },
  {
    path: "/gameroom/:pinNumber",
    name: "gameroom",
    beforeEnter: isLogin,
    component: () => import("@/views/GameroomView.vue"),
  },
  {
    path: "/member",
    name: "member",
    beforeEnter: authMember,
    component: () => import("@/views/MemberView.vue"),
    redirect: "/member/myBestcut", // /member/recentGame 기본
    children: [
      {
        path: "recentGame",
        name: "recentGame",
        beforeEnter: authMember,
        component: () => import("@/components/Member/RecentGameList.vue"),
      },
      {
        path: "starGame",
        name: "starGame",
        beforeEnter: authMember,
        component: () => import("@/components/Member/StarGameList.vue"),
      },
      {
        path: "myGame",
        name: "myGame",
        beforeEnter: authMember,
        component: () => import("@/components/Member/MyGameList.vue"),
      },
      {
        path: "myBestcut",
        name: "myBestcut",
        beforeEnter: authMember,
        component: () => import("@/components/Member/MyBestcutList.vue"),
      },
    ],
  },
  {
    path: "/admin",
    name: "admin",
    component: () => import("@/views/AdminView.vue"),
    redirect: "/admin/game",
    children: [
      {
        path: "game",
        name: "game",
        beforeEnter: authMember,
        component: () => import("@/components/Admin/GameList.vue"),
      },
      {
        path: "bestcut",
        name: "bestcut",
        beforeEnter: authMember,
        component: () => import("@/components/Admin/BestcutList.vue"),
      },
    ],
  },
  {
    path: "/login-success",
    name: "loginSuccess",
    component: () => import("@/components/common/LoginSuccess.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
