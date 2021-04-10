import { all, call, put, takeLatest, select } from "redux-saga/effects";
import { ActionTypes, SourceType } from "../constants/ActionTypes";
import API from "utils/API";
import get from "lodash/get";

export function* getItems(source) {
  // console.log('getItem', source)
  const filters = yield select(state => state.itemsReducer.filters);
  let { search = "", limit = 50, skip = 0 } = filters;
  if (search.length) {
    search = search.trim().toLowerCase();
  }
  let reqOpts = "";
  let reqUrl = "";
  let cached = true;
  switch (source) {
    case SourceType.NEWSAPI_SEARCH:
      if (search) {
        reqOpts = `q=${search}`;
      } else {
        reqOpts = `domains=thehindu.com`; // default
      }
    case SourceType.NEWSAPI_HEADLINES:
      let pageIndex = 1;
      if (skip) {
        pageIndex = 1 + parseInt(skip / limit);
      }
      reqOpts = `${reqOpts}&page=${pageIndex}&pageSize=${limit}&language=en`;
      break;
    case SourceType.RECOMMENDATIONS:
      cached = false;
      reqUrl = "list";
      reqOpts = `limit=${limit}`;
      if (skip) {
        reqOpts = `${reqOpts}&skip=${skip}`;
      }
      break;
    case SourceType.LIKED:
      reqUrl = "liked";
      cached = false;
      break;
    default:
      break;
  }
  try {
    const response = yield call(API, {
      method: "GET",
      source,
      cached,
      reqUrl,
      reqOpts
    });
    if (source === SourceType.LIKED) {
      // console.log('liked', response.data)
      const urls = response.data.map(item => item.url);
      localStorage.setItem("likes", JSON.stringify(urls));
    }
    yield put({
      type: ActionTypes.GET_ITEMS_SUCCESS,
      payload: response.data
    });
  } catch (err) {
    yield put({
      type: ActionTypes.GET_ITEMS_FAILURE,
      payload: get(err, "message", "error")
    });
  }
}

export default function* root() {
  yield all([
    takeLatest(ActionTypes.GET_ITEMS, action => getItems(action.payload))
  ]);
}
