<template>
  <div id="view">
    <div id="list">
      <my-bestcut
        v-for="bestcut in myBestcuts"
        :key="bestcut"
        :bestcut="bestcut"
        @bestcutLike="(bestcutId) => bestcutLike(bestcutId)"
        @bestcutDislike="(bestcutId) => bestcutDislike(bestcutId)"
        @deleteBestcut="(bestcutId) => deleteBestcut(bestcutId)"
      ></my-bestcut>
    </div>
    <loading-spinner id="imgLoading" v-show="isLoading">
      <!-- 이미지 로딩 중 -->
    </loading-spinner>
    <div id="noItem" v-show="(!myBestcuts || !myBestcuts.length) && !isLoading">
      베스트컷을 등록 해주세요!
    </div>
  </div>
</template>

<script setup>
import MyBestcut from "@/components/BestcutList/item/MyBestcut";
import { apiInstance } from "@/api/index";
import { useStore } from "vuex";
import { ref, computed } from "vue";
import LoadingSpinner from "./item/LoadingSpinner.vue";

const store = useStore();
const api = apiInstance();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const isLoading = ref(true);

let myBestcuts = ref([]);

let pageConditionReq = ref({
  order: "RECENT",
  period: "ALL",
  search: "MEMBER",
  keyword: "",
  page: 1,
});

const callApi = async () => {
  await api
    .get(`/api/members/bestcuts`, {
      params: {
        order: pageConditionReq.value.order,
        period: pageConditionReq.value.period,
        search: pageConditionReq.value.search,
        keyword: pageConditionReq.value.keyword,
        page: pageConditionReq.value.page,
      },
      headers: {
        "access-token": accessToken, // 변수로 가지고있는 AccessToken
      },
    })
    .then((response) => {
      // console.log("access-bestcuts: ", response.data.content); //bestcut은 pageimpl이기때문에 content까지 붙여줌
      myBestcuts.value = response.data.content;
    })
    .catch((error) => {
      console.log(error);
      // if (error.response.status == 401) {
      //   getAccessTokenByRefreshToken(); // refresh 토큰으로 다시
      // }
    })
    .finally(() => {
      isLoading.value = false;
    });
};

callApi();

store.dispatch("commonStore/setMemberTabAsync", 3);

//베스트컷 좋아요
const bestcutLike = (bestcutId) => {
  if (!accessToken) {
    alert("로그인 후 이용해주세요.");
    return;
  }
  api
    .post(
      `/api/bestcuts/like/${bestcutId}`,
      {},
      { headers: { "access-token": accessToken } }
    )
    .then(() => {
      let bestcut = myBestcuts.value.find((e) => e.bestcutId === bestcutId);
      bestcut.isLiked = true;
      bestcut.popularity++;
    })
    .catch((err) => {
      if (err.response.status === 401) {
        alert("로그인 후 이용해주세요.");
      }
    });
};

//베스트컷 좋아요 취소
const bestcutDislike = (bestcutId) => {
  api
    .delete(`/api/bestcuts/dislike/${bestcutId}`, {
      headers: { "access-token": accessToken },
    })
    .then(() => {
      let bestcut = myBestcuts.value.find((e) => e.bestcutId === bestcutId);
      bestcut.isLiked = false;
      bestcut.popularity--;
    })
    .catch((err) => {
      if (err.response.status === 401) {
        alert("로그인 후 이용해주세요.");
      }
    });
};

//베스트컷 삭제
const deleteBestcut = (bestcutId) => {
  if (window.confirm("정말 삭제하시겠습니까?")) {
    api
      .delete(`/api/bestcuts/${bestcutId}`, {
        headers: { "access-token": accessToken },
      })
      .then(() => {
        myBestcuts.value = myBestcuts.value.filter(
          (e) => e.bestcutId != bestcutId
        );
      });
  }
};
</script>

<style scoped>
#view {
  /* border: 2px solid black; */
  width: 1200px;
  position: relative;
  left: 50%;
  transform: translate(-50%, 0);
  background-color: white;
}

#list {
  display: grid;
  gap: 35px 0;
  grid-template-columns: repeat(3, 1fr);
  width: 1090px;
  margin: 2%;
}
#noItem {
  font-size: 20px;
  margin-top: 10%;
  margin-left: 25%;
}
#imgLoading {
  margin-left: 30%;
}
</style>
