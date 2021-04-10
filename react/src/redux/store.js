import { createStore, applyMiddleware } from "redux";
import createSagaMiddleware from "redux-saga";

import rootsaga from "./sagas";
import rootReducer from "./reducer";

const sagaMiddleware = createSagaMiddleware();
const store = createStore(rootReducer, applyMiddleware(sagaMiddleware));
sagaMiddleware.run(rootsaga);

export default store;
