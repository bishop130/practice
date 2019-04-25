package com.suji.lj.myapplication.Adapters;

import android.util.Log;

import com.kakao.friends.AppFriendContext;
import com.kakao.friends.response.AppFriendsResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;

public class KakaoFriends {

    public KakaoFriends(){

    }

    public void requestFriends() {

        // offset = 0, limit = 100
        AppFriendContext friendContext = new AppFriendContext(true, 0, 100, "asc");

        KakaoTalkService.getInstance().requestAppFriends(friendContext,
                new TalkResponseCallback<AppFriendsResponse>() {
                    @Override
                    public void onNotKakaoTalkUser() {
                        Log.d("카카오친구 ", "카카오 유저가 아님");
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {

                    }

                    @Override
                    public void onNotSignedUp() {

                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.d("카카오친구 ", errorResult.toString());
                    }

                    @Override
                    public void onSuccess(AppFriendsResponse result) {
                        // 친구 목록
                        Log.d("카카오친구 ", result.getFriends().toString());
                        // context의 beforeUrl과 afterUrl이 업데이트 된 상태.
                    }
                });
    }


}
