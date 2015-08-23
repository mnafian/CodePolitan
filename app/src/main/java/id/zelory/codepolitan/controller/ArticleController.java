/*
 * Copyright (c) 2015 Zelory.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package id.zelory.codepolitan.controller;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import id.zelory.benih.controller.BenihController;
import id.zelory.benih.util.BenihScheduler;
import id.zelory.benih.util.BenihUtils;
import id.zelory.benih.util.BenihWorker;
import id.zelory.codepolitan.data.Article;
import id.zelory.codepolitan.data.Category;
import id.zelory.codepolitan.data.Tag;
import id.zelory.codepolitan.data.api.CodePolitanApi;
import id.zelory.codepolitan.data.api.response.ListResponse;
import id.zelory.codepolitan.data.api.response.ObjectResponse;
import id.zelory.codepolitan.data.database.DataBaseHelper;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : July 29, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ArticleController extends BenihController<ArticleController.Presenter>
{
    private Article article;
    private List<Article> articles;

    public ArticleController(Presenter presenter)
    {
        super(presenter);
    }

    public void loadArticle(int id)
    {
        presenter.showLoading();
        CodePolitanApi.pluck()
                .getApi()
                .getDetailArticle(id)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(ObjectResponse::getResult)
                .map(article -> {
                    article.setBookmarked(DataBaseHelper.pluck().isBookmarked(article.getId()));
                    article.setReadLater(DataBaseHelper.pluck().isReadLater(article.getId()));
                    return article;
                })
                .subscribe(article -> {
                    this.article = article;
                    if (presenter != null)
                    {
                        presenter.showArticle(article);
                        presenter.dismissLoading();
                    }
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                    if (presenter != null)
                    {
                        presenter.showError(throwable);
                        presenter.dismissLoading();
                    }
                });
    }

    public void loadArticles(int page)
    {
        presenter.showLoading();
        CodePolitanApi.pluck()
                .getApi()
                .getLatestArticles(page)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(ListResponse::getResult)
                .flatMap(Observable::from)
                .map(article -> {
                    article.setBookmarked(DataBaseHelper.pluck().isBookmarked(article.getId()));
                    article.setReadLater(DataBaseHelper.pluck().isReadLater(article.getId()));
                    article.setBig(BenihUtils.randInt(0, 8) == 5);
                    return article;
                })
                .toList()
                .subscribe(articles -> {
                    BenihWorker.pluck()
                            .doInNewThread(() -> {
                                if (page == 1)
                                {
                                    articles.get(0).setBig(true);
                                    this.articles = articles;
                                } else
                                {
                                    this.articles.addAll(articles);
                                }
                            }).subscribe(o -> {
                        if (presenter != null)
                        {
                            presenter.showArticles(articles);
                        }
                    });
                    if (presenter != null)
                    {
                        presenter.dismissLoading();
                    }
                }, throwable -> {
                    if (presenter != null)
                    {
                        presenter.showError(throwable);
                        presenter.dismissLoading();
                    }
                });
    }

    public void loadArticles(String postType, int page)
    {
        presenter.showLoading();
        CodePolitanApi.pluck()
                .getApi()
                .getLatestArticles(postType, page)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .subscribe(articleResponse -> {
                    if (articleResponse.getCode())
                    {
                        BenihWorker.pluck()
                                .doInNewThread(() -> {
                                    if (page == 1)
                                    {
                                        articles = articleResponse.getResult();
                                    } else
                                    {
                                        articles.addAll(articleResponse.getResult());
                                    }
                                }).subscribe(o -> {
                            if (presenter != null)
                            {
                                presenter.showArticles(articleResponse.getResult());
                            }
                        });
                    }
                    if (presenter != null)
                    {
                        presenter.dismissLoading();
                    }
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                    if (presenter != null)
                    {
                        presenter.showError(throwable);
                        presenter.dismissLoading();
                    }
                });
    }

    public void loadArticles(Category category, int page)
    {
        presenter.showLoading();
        CodePolitanApi.pluck()
                .getApi()
                .getArticles(category, page)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .subscribe(articleResponse -> {
                    if (articleResponse.getCode())
                    {
                        BenihWorker.pluck()
                                .doInNewThread(() -> {
                                    if (page == 1)
                                    {
                                        articles = articleResponse.getResult();
                                    } else
                                    {
                                        articles.addAll(articleResponse.getResult());
                                    }
                                }).subscribe(o -> {
                            if (presenter != null)
                            {
                                presenter.showArticles(articleResponse.getResult());
                            }
                        });
                    }
                    if (presenter != null)
                    {
                        presenter.dismissLoading();
                    }
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                    if (presenter != null)
                    {
                        presenter.showError(throwable);
                        presenter.dismissLoading();
                    }
                });
    }

    public void loadArticles(Tag tag, int page)
    {
        presenter.showLoading();
        CodePolitanApi.pluck()
                .getApi()
                .getArticles(tag, page)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .subscribe(articleResponse -> {
                    if (articleResponse.getCode())
                    {
                        BenihWorker.pluck()
                                .doInNewThread(() -> {
                                    if (page == 1)
                                    {
                                        articles = articleResponse.getResult();
                                    } else
                                    {
                                        articles.addAll(articleResponse.getResult());
                                    }
                                }).subscribe(o -> {
                            if (presenter != null)
                            {
                                presenter.showArticles(articleResponse.getResult());
                            }
                        });
                    }
                    if (presenter != null)
                    {
                        presenter.dismissLoading();
                    }
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                    if (presenter != null)
                    {
                        presenter.showError(throwable);
                        presenter.dismissLoading();
                    }
                });
    }

    public void filter(String query)
    {
        Observable.from(articles)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                .filter(article -> article.getTitle().toLowerCase().contains(query.toLowerCase()))
                .map(article -> {
                    article.setBookmarked(DataBaseHelper.pluck().isBookmarked(article.getId()));
                    article.setReadLater(DataBaseHelper.pluck().isReadLater(article.getId()));
                    return article;
                })
                .toList()
                .subscribe(presenter::showFilteredArticles, presenter::showError);
    }


    @Override
    public void loadState(Bundle bundle)
    {
        article = bundle.getParcelable("article");
        if (article != null)
        {
            article.setBookmarked(DataBaseHelper.pluck().isBookmarked(article.getId()));
            presenter.showArticle(article);
        } else
        {
            presenter.showError(new Throwable("Article is null"));
        }

        articles = bundle.getParcelableArrayList("articles");
        if (articles != null)
        {
            Observable.from(articles)
                    .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                    .map(article -> {
                        article.setBookmarked(DataBaseHelper.pluck().isBookmarked(article.getId()));
                        article.setReadLater(DataBaseHelper.pluck().isReadLater(article.getId()));
                        return article;
                    })
                    .toList()
                    .subscribe(presenter::showArticles, presenter::showError);
        } else
        {
            presenter.showError(new Throwable("List article is null"));
        }
    }

    @Override
    public void saveState(Bundle bundle)
    {
        bundle.putParcelable("article", article);
        bundle.putParcelableArrayList("articles", (ArrayList<Article>) articles);
    }

    public interface Presenter extends BenihController.Presenter
    {
        void showArticle(Article article);

        void showArticles(List<Article> articles);

        void showFilteredArticles(List<Article> articles);

        void showLoading();

        void dismissLoading();
    }
}
