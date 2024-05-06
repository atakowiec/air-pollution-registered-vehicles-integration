import React from 'react'
import ReactDOM from 'react-dom/client'
import App from "./App.tsx";
import {store} from "./store";
import {Provider} from "react-redux";
import {ReloadApiProvider} from "./hooks/reload-api/ReloadApiContext.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Provider store={store}>
      <ReloadApiProvider>
        <App/>
      </ReloadApiProvider>
    </Provider>
  </React.StrictMode>
)
