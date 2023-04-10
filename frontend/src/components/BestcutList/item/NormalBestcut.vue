<template>
  <div id="content">
    <div id="top-section">
      <img
        :src="`${IMAGE_PATH}/${props.bestcut.bestcutImgUrl}`"
        alt="베스트컷"
        @click="setCurrentModalAsync(`bestcutDetail`)"
        class="image"
      />
      <div class="image-behind2">
        <img
          :src="`${IMAGE_PATH}/${props.bestcut.gameImgUrl}`"
          alt="원본사진"
          @click="setCurrentModalAsync(`bestcutDetail`)"
          class="image"
        />
      </div>
    </div>
    <div id="bottom-section">
      <img
        :src="`${IMAGE_PATH}/${props.bestcut.profileImgUrl}`"
        alt="유저프로필사진"
        class="profile-image"
      />
      <div id="nickname">
        <span>{{ props.bestcut.nickname }}</span>
      </div>
      <div id="bestcut-title">
        <span>{{ props.bestcut.bestcutImgTitle }}</span>
      </div>
      <div id="game-desc">
        <span>{{ props.bestcut.gameTitle }}</span> |
        <span>{{ props.bestcut.gameImgDesc }}</span>
      </div>

      <div id="etc-section" v-click-outside-element="onClickOutside">
        <div id="etc-button" @click="open">
          <img
            :src="require(`@/assets/images/etc-button.png`)"
            alt="기타버튼"
            class="etc"
          />
        </div>
        <div id="etc" v-show="state">
          <div @click="setCurrentModalAsync(`bestcutReport`)">
            <span>신고</span>
          </div>
        </div>
      </div>

      <button
        v-if="!props.bestcut.isLiked"
        @click="bestcutLike(props.bestcut.bestcutId)"
        class="like-button"
      >
        <img
          :src="require(`@/assets/images/like-button.svg`)"
          alt="좋아요아이콘"
          class="like"
        />
        <span>{{ props.bestcut.popularity }}</span>
      </button>
      <button
        v-else
        @click="bestcutDislike(props.bestcut.bestcutId)"
        class="like-button"
      >
        <img
          :src="require(`@/assets/images/dislike-button.svg`)"
          alt="좋아요취소아이콘"
          class="dislike"
        />
        <span>{{ props.bestcut.popularity }}</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref } from "vue";
import { useStore } from "vuex";

const props = defineProps({ bestcut: Object });
const emit = defineEmits(["bestcutLike", "bestcutDislike"]);
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;
const state = ref(false);
const store = useStore();

const onClickOutside = () => {
  state.value = false;
};

const open = () => {
  state.value = !state.value;
};

const bestcutLike = (bestcutId) => {
  emit("bestcutLike", bestcutId);
};

const bestcutDislike = (bestcutId) => {
  emit("bestcutDislike", bestcutId);
};

const setCurrentModalAsync = (what) => {
  open();
  store.dispatch("commonStore/setCurrentModalAsync", {
    name: what,
    data: props.bestcut,
  });
};
</script>

<style scoped>
#content {
  border: 2px solid black;
  width: 325px;
  height: 380px;
}

#nickname {
  width: 95%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline;
  position: absolute;
  margin-top: 4px;
  margin-left: 10px;
}
#game-desc {
  margin-top: 15px;
  width: 80%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
#bestcut-title {
  margin-top: 15px;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

#nickname span {
  font-size: 22px;
}
#bestcut-title span {
  font-size: 16px;
  color: #656565;
}
#game-desc span {
  font-size: 16px;
  color: #656565;
}
#top-section {
  position: relative;
}
#top-section:hover {
  cursor: pointer;
}
.image {
  width: 325px;
  height: 260px;
  object-fit: cover;
  display: inline-block;
}

.profile-image {
  width: 30px;
  height: 30px;
  border-radius: 75%;
}

.image:hover ~ .image-behind2 {
  display: inline-block;
}

.image-behind {
  position: absolute;
  top: 0;
  left: 0;
  width: 325px;
  height: 260px;
  background-color: black;
  opacity: 0.5;
}
.image-behind2 {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  display: none;
}
#bottom-section {
  padding: 5px 10px;
  position: relative;
}
#etc-button {
  position: absolute;
  top: 0px;
  left: 290px;
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

.like-button {
  position: absolute;
  background-color: #d9d9d9;
  border-radius: 30px;
  border-width: 0;
  width: 60px;
  height: 30px;
  top: 78px;
  left: 255px;
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
</style>
