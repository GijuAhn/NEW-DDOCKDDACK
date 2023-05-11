import { createRouter, createWebHistory } from "vue-router";
// import { useStore } from "vuex";
// import { computed } from "vue";
// import store from "@/store";
import MainView from "@/views/MainView.vue";

// const authMember = async (to, from, next) => {
//   let accessToken = computed(() => store.state.memberStore.accessToken).value;
//   let memberInfo = computed(() => store.state.memberStore.memberInfo).value;

//   if (accessToken === "") {
//     await store.dispatch("memberStore/accesstokenReissue", accessToken);
//     accessToken = computed(() => store.state.memberStore.accessToken).value;
//   }
//   if (accessToken !== "" && memberInfo.email === "") {
//     await store.dispatch("memberStore/getMemberInfo");
//   }
//   accessToken = computed(() => store.state.memberStore.accessToken).value;
//   next();
// };

// const isLogin = async () => {
//   let accessToken = computed(() => store.state.memberStore.accessToken).value;
//   let memberInfo = computed(() => store.state.memberStore.memberInfo).value;

//   if (accessToken !== "") {
//     await store.dispatch("memberStore/accesstokenReissue", accessToken);
//     accessToken = computed(() => store.state.memberStore.accessToken).value;
//   }
//   if (accessToken !== "" && memberInfo.email === "") {
//     await store.dispatch("memberStore/getMemberInfo");
//   }
//   accessToken = computed(() => store.state.memberStore.accessToken).value;
// };

const routes = [
  {
    path: "/",
    name: "main",
    component: MainView,
  },
  {
    path: "/single-games",
    name: "singleGames",
    component: () => import("@/views/SingleGameListView.vue"),
  },
  {
    path: "/multi-games",
    name: "multiGames",
    component: () => import("@/views/MultiGameListView.vue"),
  },
  {
    path: "/gameMake",
    name: "gameMake",
    component: () => import("@/views/GameMakeView.vue"),
    redirect: "/gameMake/createGame",
    children: [
      {
        path: "createGame",
        name: "createGame",
        component: () => import("@/components/GameMake/CreateGame.vue"),
      },
    ],
  },
  {
    path: "/bestcut",
    name: "bestcutList", //bestcut 중복 체크
    component: () => import("@/views/BestcutView.vue"),
  },
  {
    path: "/gameroom/:pinNumber",
    name: "gameroom",
    component: () => import("@/views/GameroomView.vue"),
  },
  {
    path: "/member",
    name: "member",
    component: () => import("@/views/MemberView.vue"),
    redirect: "/member/myBestcut", // /member/recentGame 기본
    children: [
      {
        path: "starGame",
        name: "starGame",
        component: () => import("@/components/Member/StarGameList.vue"),
      },
      {
        path: "myGame",
        name: "myGame",
        component: () => import("@/components/Member/MyGameList.vue"),
      },
      {
        path: "myBestcut",
        name: "myBestcut",
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
        component: () => import("@/components/Admin/GameList.vue"),
      },
      {
        path: "bestcut",
        name: "bestcut",
        component: () => import("@/components/Admin/BestcutList.vue"),
      },
    ],
  },
  {
    path: "/login-success",
    name: "loginSuccess",
    component: () => import("@/components/common/LoginSuccess.vue"),
  },
  {
    path: "/mobile/single-games",
    name: "mobileSingleGame",
    component: () => import("@/views/MobileView.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
