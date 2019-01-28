package com.playgilround.schedule.client.fragment;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.model.Schedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * 19-01-23
 * 저장된 스케줄 정보가 나오는 Activity
 */
public class DetailScheduleFragment extends android.app.DialogFragment {

    @BindView(R.id.ivMap)
    ImageView ivMap;

    @BindView(R.id.ivBtnLaunch)
    ImageButton ivBtn;

    @BindView(R.id.linearBackView)
    LinearLayout backView;

    @BindView(R.id.linearBackButton)
    LinearLayout backBtnView;

    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.tvScheduleTitle)
    TextView tvTitle;

    @BindView(R.id.tvScheduleLocation)
    TextView tvLocation;

    static String strDate;
    static int scheduleId;
    Realm realm;

    float pixelDensity;
    Animation animation;

    boolean flag = true;

    static final String TAG = DetailScheduleFragment.class.getSimpleName();

    public static DetailScheduleFragment getInstance(String date, int id) {
        strDate = date;
        scheduleId = id;

        return new DetailScheduleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_schedule, container);
        ButterKnife.bind(this, rootView);

        pixelDensity = getResources().getDisplayMetrics().density;

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_anim);
        findScheduleInfo();
        return rootView;
    }

    private void findScheduleInfo() {
        realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm -> {
            Schedule schedule = realm.where(Schedule.class).equalTo("id", scheduleId).findFirst();
            DateTime dateTime = new DateTime(Long.valueOf(schedule.getTime()), DateTimeZone.UTC);
            String strDay = dateTime.plusHours(9).toString(getString(R.string.text_date_day_time));
            String strTitle = schedule.getTitle();
            String strLocation = schedule.getLocation();

            tvTime.setText(strDay);
            tvTitle.setText(strTitle);
            tvLocation.setText(strLocation);
        });
    }

    @OnClick(R.id.ivBtnLaunch)
    public void onButtonLaunch() {

        int x = ivMap.getRight();
        int y = ivMap.getBottom();

        x -= ((28 * pixelDensity) + (16 * pixelDensity));

        int hypotenuse = (int) Math.hypot(ivMap.getWidth(), ivMap.getHeight());

        if (flag) {
            ivBtn.setBackgroundResource(R.drawable.rounded_cancel_button);
            ivBtn.setImageResource(R.drawable.ic_chevron_left_black_24dp);

            FrameLayout.LayoutParams paramters = (FrameLayout.LayoutParams)
                    backView.getLayoutParams();

            paramters.height = ivMap.getHeight();
            backView.setLayoutParams(paramters);

            //Animation 효과
            Animator anim = ViewAnimationUtils.createCircularReveal(backView, x, y, 0, hypotenuse);
            anim.setDuration(700);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    backBtnView.setVisibility(View.VISIBLE);
                    backBtnView.startAnimation(animation);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            backView.setVisibility(View.VISIBLE);
            anim.start();

            flag = false;
        } else {
            //다시 클릭시 되돌아 오기.
            ivBtn.setBackgroundResource(R.drawable.rounded_button);
            ivBtn.setImageResource(R.drawable.ic_chevron_right_black_24dp);

            Animator anim = ViewAnimationUtils.createCircularReveal(backView, x, y, hypotenuse, 0);
            anim.setDuration(400);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    backView.setVisibility(View.GONE);
                    backBtnView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            anim.start();
            flag = true;
        }
    }
}
