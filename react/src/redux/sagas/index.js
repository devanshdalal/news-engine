import { all, fork } from "redux-saga/effects";

// import user from "./user";
import getItems from "./getItem";

export default function* root() {
  yield all([fork(getItems)]);
}
