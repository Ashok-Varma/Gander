package com.ashokvarma.gander.internal.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.support.FormatUtils;
import com.ashokvarma.gander.internal.support.TransactionColorUtil;
import com.ashokvarma.gander.internal.ui.BaseGanderActivity;
import com.ashokvarma.gander.internal.ui.details.fragments.TransactionFragment;
import com.ashokvarma.gander.internal.ui.details.fragments.TransactionOverviewFragment;
import com.ashokvarma.gander.internal.ui.details.fragments.TransactionPayloadFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class TransactionDetailsActivity extends BaseGanderActivity {

    private static final String ARG_TRANSACTION_ID = "transaction_id";
    private static final String ARG_TRANSACTION_STATUS = "transaction_status";
    private static final String ARG_TRANSACTION_RESPONSE_CODE = "transaction_response_code";

    private static int selectedTabPosition = 0;

    public static void start(Context context, long transactionId, HttpTransaction.Status status, Integer responseCode) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        intent.putExtra(ARG_TRANSACTION_ID, transactionId);
        intent.putExtra(ARG_TRANSACTION_STATUS, status.ordinal());
        intent.putExtra(ARG_TRANSACTION_RESPONSE_CODE, responseCode == null ? -1 : responseCode);
        context.startActivity(intent);
    }

    TextView title;
    Adapter adapter;
    AppBarLayout appBarLayout;

    private HttpTransaction transaction;
    TransactionDetailViewModel viewModel;
    TransactionColorUtil colorUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gander_activity_transaction);
        long transactionId = getIntent().getLongExtra(ARG_TRANSACTION_ID, 0);
        int statusOrdinal = getIntent().getIntExtra(ARG_TRANSACTION_STATUS, HttpTransaction.Status.Requested.ordinal());
        int responseCode = getIntent().getIntExtra(ARG_TRANSACTION_RESPONSE_CODE, -1);
        colorUtil = TransactionColorUtil.getInstance(this);

        appBarLayout = findViewById(R.id.gander_details_appbar);
        appBarLayout.setBackgroundColor(colorUtil.getTransactionColor(HttpTransaction.Status.values()[statusOrdinal], responseCode));
        Toolbar toolbar = findViewById(R.id.gander_details_toolbar);
        setSupportActionBar(toolbar);
        title = findViewById(R.id.gander_details_toolbar_title);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        ViewPager viewPager = findViewById(R.id.gander_details_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = findViewById(R.id.gander_details_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewModel = ViewModelProviders.of(this).get(TransactionDetailViewModel.class);
        viewModel.getTransactionWithId(transactionId).observe(TransactionDetailsActivity.this, new Observer<HttpTransaction>() {
            @Override
            public void onChanged(@Nullable HttpTransaction transaction) {
                TransactionDetailsActivity.this.transaction = transaction;
                populateUI();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gander_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_text) {
            if (transaction != null)
                share(FormatUtils.getShareText(this, transaction));
            return true;
        } else if (item.getItemId() == R.id.share_curl) {
            if (transaction != null)
                share(FormatUtils.getShareCurlCommand(transaction));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void populateUI() {
        if (transaction != null) {
            title.setText(transaction.getMethod().concat(" ").concat(transaction.getPath()));
            for (TransactionFragment fragment : adapter.fragments) {
                fragment.transactionUpdated(transaction);
            }
            appBarLayout.setBackgroundColor(colorUtil.getTransactionColor(transaction));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TransactionOverviewFragment(), getString(R.string.gander_overview));
        adapter.addFragment(TransactionPayloadFragment.newInstance(TransactionPayloadFragment.TYPE_REQUEST), getString(R.string.gander_request));
        adapter.addFragment(TransactionPayloadFragment.newInstance(TransactionPayloadFragment.TYPE_RESPONSE), getString(R.string.gander_response));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new SimpleOnPageChangedListener() {
            @Override
            public void onPageSelected(int position) {
                selectedTabPosition = position;
            }
        });
        viewPager.setCurrentItem(selectedTabPosition);
    }

    private void share(String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, null));
    }

    static class Adapter extends FragmentPagerAdapter {
        final List<TransactionFragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(TransactionFragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
