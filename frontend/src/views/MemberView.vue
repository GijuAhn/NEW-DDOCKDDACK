<template>
  <navigation-bar-in-member-page />
  <router-view></router-view>
</template>

<script setup>
import NavigationBarInMemberPage from "@/components/common/NavigationBarInMemberPage";
// import MyProfile from "@/components/common/MyProfile";
import { useStore } from "vuex";
// import { computed } from "vue";
// import { apiInstance } from "@/api/index";
// import { ref } from "vue";

// const api = apiInstance();
const store = useStore();
// const userid = computed(() => store.state.memberStore.memberInfo.id).value;
// const pageConditionReq = {
//   order: "RECENT",
//   period: "ALL",
//   search: "MEMBER",
//   keyword: "",
//   page: 1,
// };

// store.dispatch("memberStore/getMemberInfo");

// const myProfile = ref();

// const callApi = () => {
//   console.log("myProfile PRINT");
//   api
//     .get(`/api/members/1`, {
//       headers: {
//         "access-token":
//           "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2NzU1MjIzODQsImV4cCI6MTY3NjI0MjM4NH0.6kuEJj0P3ybQ6Cu4AgtqLNf3HJxWGSYyJc9m6T7wvO0", // 변수로 가지고있는 AccessToken
//       },
//     })
//     .then((response) => {
//       console.log(response);
//       myProfile.value = response.data.content;
//     })
//     .catch((error) => {
//       console.log(error);
//       if (error.response.status != 401) {
//         // getAccessTokenByRefreshToken(); // refresh 토큰으로 다시
//       }
//     });
// };

// callApi();

store.dispatch("commonStore/setColorAsync", "default");
</script>

<style></style>
