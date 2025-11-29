# device-shake

Capacitor plugin to detect device shaking

## Install

```bash
npm install device-shake
npx cap sync
```

## API

<docgen-index>

* [`enableListening()`](#enablelistening)
* [`stopListening()`](#stoplistening)
* [`addListener('shake', ...)`](#addlistenershake-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### enableListening()

```typescript
enableListening() => Promise<void>
```

--------------------


### stopListening()

```typescript
stopListening() => Promise<void>
```

--------------------


### addListener('shake', ...)

```typescript
addListener(eventName: 'shake', listenerFunc: () => void) => Promise<PluginListenerHandle>
```

| Param              | Type                       |
| ------------------ | -------------------------- |
| **`eventName`**    | <code>'shake'</code>       |
| **`listenerFunc`** | <code>() =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
