<template>
  <div class="white-bg">
    <div class="top-section">
      <div>
        <div id="bestcut-title">
          <span>{{ currentModal.data.bestcutImgTitle }}</span>
        </div>
        <button
          v-if="!currentModal.data.isLiked"
          @click="bestcutLike(currentModal.data.bestcutId)"
          class="like-button"
        >
          <img
            :src="require(`@/assets/images/like-button.svg`)"
            alt="좋아요아이콘"
            class="like"
          />
          <span>{{ currentModal.data.popularity }}</span>
        </button>
        <button
          v-else
          @click="bestcutDislike(currentModal.data.bestcutId)"
          class="like-button"
        >
          <img
            :src="require(`@/assets/images/dislike-button.svg`)"
            alt="좋아요취소아이콘"
            class="dislike"
          />
          <span>{{ currentModal.data.popularity }}</span>
        </button>
      </div>
      <div>
        <img
          :src="`https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/340px-Default_pfp.svg.png?20220226140232`"
          alt="유저프로필사진"
          class="profile-image"
        />
        <div id="nickname">
          <span>{{ currentModal.data.nickname }} | {{ date }}</span>
        </div>
      </div>
    </div>

    <hr />
    <div class="img-section">
      <img
        :src="`${IMAGE_PATH}/${currentModal.data.gameImgUrl}`"
        alt="원본사진"
        class="image"
      />
      <img
        :src="`${IMAGE_PATH}/${currentModal.data.bestcutImgUrl}`"
        alt="베스트컷"
        class="image"
      />
    </div>
    <hr />

    <div class="bottom-section">
      <div id="game-info">
        <span>
          {{ currentModal.data.gameTitle }} |
          {{ currentModal.data.gameImgDesc }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { computed } from "vue";
import { useStore } from "vuex";

const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;
const store = useStore();
const currentModal = computed(() => store.state.commonStore.currentModal);
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const date = computed(() => currentModal.value.data.createdDate.substr(0, 10));
const api = apiInstance();

//베스트컷 좋아요
const bestcutLike = () => {
  if (!accessToken) {
    alert("로그인 후 이용해주세요.");
    return;
  }
  api
    .post(
      `/api/bestcuts/like/${currentModal.value.data.bestcutId}`,
      {},
      { headers: { "access-token": accessToken.value } }
    )
    .then(() => {
      currentModal.value.data.isLiked = true;
      currentModal.value.data.popularity++;
    });
};

//베스트컷 좋아요 취소
const bestcutDislike = () => {
  if (!accessToken) {
    alert("로그인 후 이용해주세요.");
    return;
  }
  api
    .delete(`/api/bestcuts/dislike/${currentModal.value.data.bestcutId}`, {
      headers: { "access-token": accessToken.value },
    })
    .then(() => {
      currentModal.value.data.isLiked = false;
      currentModal.value.data.popularity--;
    });
};
</script>

<style scoped>
.white-bg {
  width: 100%;
  background: white;
  padding: 20px;
  width: 1020px;
  height: 560px;
  border-radius: 10px;
}

#bestcut-title {
  width: 95%;
  margin-top: 26px;
  margin-left: 40px;
}

#bestcut-title span {
  font-size: 40px;
}

.like-button {
  position: absolute;
  background-color: #d9d9d9;
  border-radius: 30px;
  border-width: 0;
  width: 60px;
  height: 30px;
  top: 50px;
  right: 87px;
}
.like-button:hover {
  cursor: pointer;
}
.like-button span {
  position: absolute;
  top: 30%;
  left: 50%;
  margin-left: 8px;
}
.like {
  width: 35%;
  height: 60%;
  left: 10px;
  top: 6px;
  position: absolute;
}
.dislike {
  width: 35%;
  height: 60%;
  left: 10px;
  top: 6px;
  position: absolute;
}

.like-button:hover .like {
  filter: invert(100%) sepia(0%) saturate(6054%) hue-rotate(358deg)
    brightness(97%) contrast(113%);
}

.like-button:hover span {
  color: #fff;
}

#bottom-section {
  padding: 5px 10px;
  position: relative;
}
#etc-button {
  position: absolute;
  top: 50px;
  right: 50px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  text-align: center;
}
#etc-button:hover {
  background-color: #d9d9d9;
  cursor: pointer;
  transition: 0.3s;
}
.etc {
  width: 50%;
  height: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

#etc {
  position: absolute;
  top: 30px;
  left: 290px;
  background-color: white;
  width: 130px;
  box-shadow: 0 0 10px #8b8b8b;
  z-index: 1;
  border-radius: 10px;
  padding: 10px 0;
  display: block;
}
#etc :hover {
  cursor: pointer;
}
#etc span {
  font-size: 16px;
  display: block;
  padding: 10px;
}
#etc span:hover {
  background-color: #d9d9d9;
}

.profile-image {
  widows: 30px;
  height: 30px;
  margin-top: 14px;
  margin-left: 40px;
  display: inline-block;
}

#nickname {
  margin-top: 18px;
  width: 100%;
  display: inline;
  position: absolute;
  margin-left: 3px;
}

#nickname span {
  font-size: 20px;
  font-weight: 100;
  font-style: normal;
  color: #6d6d6d;
}

hr {
  width: 938px;
  margin-top: 14px;
  margin-left: 32px;
  height: 2px;
  border: 0;
  background-color: #d6d6d6;
}

.image {
  width: 400px;
  height: 360px;
  object-fit: cover;
  display: inline-block;
  margin-top: 14px;
  margin-left: 68px;
}

#game-info {
  width: 100%;
  margin-top: 8px;
}

#game-info span {
  float: right;
  margin-right: 50px;
  color: #6d6d6d;
}
</style>
